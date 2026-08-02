package com.example.LeetDeCode_Battle_Module.service;

import com.example.LeetDeCode_Battle_Module.DTO.BattleRoomView;
import com.example.LeetDeCode_Battle_Module.DTO.ProblemStatement;
import com.example.LeetDeCode_Battle_Module.exceptionhandler.ResourceNotFoundException;
import com.example.LeetDeCode_Battle_Module.exceptionhandler.RoomFullException;
import com.example.LeetDeCode_Battle_Module.model.Battle;
import com.example.LeetDeCode_Battle_Module.model.BattleRoom;
import com.example.LeetDeCode_Battle_Module.model.Userpoints;
import com.example.LeetDeCode_Battle_Module.repository.BattleRepository;
import com.example.LeetDeCode_Battle_Module.repository.UserPointsRepository;
import com.example.LeetDeCode_Battle_Module.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProblemService problemService;
    @Autowired
    private UserPointService userPointService;
    @Autowired
    private BattleRepository battleRepository;
    @Autowired
    private UserPointsRepository userPointsRepository;

    private static final Duration ROOM_TTL = Duration.ofHours(3);
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ROOM_TOPIC_PREFIX = "/topic/room/";

    public BattleRoomView createRoom(String hostPlayerId, String hostUsername, String topic, String level) {
        ProblemStatement problem = problemService.getRandomProblem(topic, level);
        Userpoints userpoints=userPointsRepository.findByUsername(hostUsername);
        if(userpoints==null){
            userpoints = new Userpoints();
            userpoints.setUsername(hostUsername);
            userpoints=userPointsRepository.save(userpoints);
        }
        BattleRoom room = BattleRoom.builder()
                .roomCode(generateRoomCode())
                .hostPlayerId(hostPlayerId)
                .problemId(problem.getId())
                .status(BattleRoom.RoomStatus.WAITING_FOR_PLAYERS)
                .createdAt(LocalDateTime.now())
                .players(List.of(BattleRoom.PlayerState.builder()
                        .playerId(hostPlayerId)
                        .username(hostUsername)
                        .score(0)
                        .submitted(false)
                        .build()))
                .build();
        Battle battle = new Battle();
        battle.setRoomCode(room.getRoomCode());
        battle.setProblemId(problem.getId());
        battle.setLevel(level);
        battle.setTopic(topic);
        battle.setPlayerA(userpoints);
        battleRepository.save(battle);

        return saveRoom(room); // persists + broadcasts + returns safe view
    }

    public BattleRoomView joinRoom(String roomCode, String playerId, String username) {
        BattleRoom room = getRoomInternal(roomCode);

        Userpoints userpoints=userPointsRepository.findByUsername(username);
        if(userpoints==null){
            userpoints = new Userpoints();
            userpoints.setUsername(username);
            userpoints=userPointsRepository.save(userpoints);
        }

        if (room.getStatus() != BattleRoom.RoomStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Room is not accepting players: " + roomCode);
        }

        boolean alreadyJoined = room.getPlayers().stream()
                .anyMatch(p -> p.getPlayerId().equals(playerId));

        int count=room.getPlayers().size();
        if(count>1 || alreadyJoined){
            throw new RoomFullException("Room is Occupied");
        }
            room.setStatus(BattleRoom.RoomStatus.IN_PROGRESS);
            room.getPlayers().add(BattleRoom.PlayerState.builder()
                    .playerId(playerId)
                    .username(username)
                    .score(0)
                    .submitted(false)
                    .build());
            Battle battle=battleRepository.findByRoomCode(room.getRoomCode());
            battle.setPlayerB(userpoints);
            battleRepository.save(battle);
        return saveRoom(room);
    }

    public BattleRoomView submitPlayerCode(String roomCode, String playerId, String language, String finalCode,int score,boolean allpassed) {
        BattleRoom room = getRoomInternal(roomCode);

        if(room.getStatus() ==  BattleRoom.RoomStatus.COMPLETED){
            throw new RoomFullException("Room is already completed");
        }

        room.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .ifPresentOrElse(
                        p -> {
                            p.setScore(score);
                            p.setCurrentCode(finalCode);
                            p.setLanguage(language);
                            p.setSubmitted(true);
                            p.setSubmittedAt(java.time.Instant.now());
                        },
                        () -> { throw new IllegalStateException("Player not in room: " + playerId); }
                );
        if(room.getWinnerPlayerId() == null && allpassed){
            room.setWinnerPlayerId(playerId);
            userPointService.updateUserPoints(playerId,10);
            Battle battle=battleRepository.findByRoomCode(roomCode);
            battle.setWinnerId(playerId);
            battleRepository.save(battle);
        }
        boolean allSubmitted = room.getPlayers().stream().allMatch(BattleRoom.PlayerState::isSubmitted);
        if (allSubmitted) {
            room.setStatus(BattleRoom.RoomStatus.COMPLETED);
         //   eventPublisher.publishEvent(new RoomCompletedEvent(roomCode)); // NEW
        }

        return saveRoom(room);
    }

    /** Public read-only snapshot — safe for controllers (e.g. on initial subscribe). */
    public BattleRoomView getRoomView(String roomCode) {
        BattleRoomView cachedView = (BattleRoomView) redisTemplate.opsForValue()
                .get(RedisKeyUtil.roomViewKey(roomCode));
        if (cachedView != null) {
            return cachedView;
        }

        BattleRoomView view = BattleRoomView.from(getRoomInternal(roomCode));
        return updateBattleRoomViewDetails(view);
    }

    public BattleRoomView updateBattleRoomViewDetails(BattleRoomView view) {
        if (view == null || view.getRoomCode() == null || view.getRoomCode().isBlank()) {
            throw new IllegalArgumentException("BattleRoomView roomCode is required");
        }

        redisTemplate.opsForValue().set(RedisKeyUtil.roomViewKey(view.getRoomCode()), view, ROOM_TTL);
        return view;
    }

    /**
     * Raw internal lookup — NEVER return this from a public method or hand it to a
     * controller. Used only within this service (mutations) and by Phase 4's match
     * conclusion logic, which legitimately needs currentCode for final grading.
     */
    BattleRoom getRoomInternal(String roomCode) {
        BattleRoom room = (BattleRoom) redisTemplate.opsForValue().get(RedisKeyUtil.roomKey(roomCode));
        if (room == null) {
            throw new ResourceNotFoundException("Room not found: " + roomCode);
        }
        return room;
    }

    /**
     * Single choke point for ALL room mutations: persists to Redis (refreshing TTL),
     * broadcasts the safe view to /topic/room/{roomCode}, and returns that same view
     * so callers (controllers) only ever see the sanitized shape.
     */
    private BattleRoomView saveRoom(BattleRoom room) {
        redisTemplate.opsForValue().set(RedisKeyUtil.roomKey(room.getRoomCode()), room, ROOM_TTL);
        BattleRoomView view = BattleRoomView.from(room);
        updateBattleRoomViewDetails(view);
        //messagingTemplate.convertAndSend(ROOM_TOPIC_PREFIX + room.getRoomCode(), view);
        return view;
    }

    private String generateRoomCode() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
