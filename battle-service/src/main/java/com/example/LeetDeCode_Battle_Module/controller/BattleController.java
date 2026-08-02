package com.example.LeetDeCode_Battle_Module.controller;

import com.example.LeetDeCode_Battle_Module.DTO.BattleHistoryDTO;
import com.example.LeetDeCode_Battle_Module.DTO.BattleRequest;
import com.example.LeetDeCode_Battle_Module.DTO.BattleRoomView;
import com.example.LeetDeCode_Battle_Module.DTO.JoinBattleRequest;
import com.example.LeetDeCode_Battle_Module.response.ApiResponse;
import com.example.LeetDeCode_Battle_Module.service.RoomService;
import com.example.LeetDeCode_Battle_Module.service.UserPointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/battle")
@RequiredArgsConstructor
public class BattleController {

    private final RoomService roomService;

    @Autowired
    private UserPointService  userPointService;


    @PostMapping("/create")
    public ApiResponse<BattleRoomView> createBattle(@Valid  @RequestBody BattleRequest request) {
        boolean a=userPointService.addUser(request.getHostUsername());
        ApiResponse<BattleRoomView> response = new ApiResponse<>();
        try {
            BattleRoomView battle = roomService.createRoom(
                    request.getHostPlayerId(),
                    request.getHostUsername(),
                    request.getTopic(),
                    request.getLevel()
            );
            response.setData(battle);
            response.setMessage("success");
            response.setStatus(true);
        }catch (Exception e){
            response.setMessage(e.getMessage());
            response.setStatus(false);
        }
        return response;
    }

    @PutMapping("/join")
    public ApiResponse<BattleRoomView> joinBattle(@Valid @RequestBody JoinBattleRequest request) {

        ApiResponse<BattleRoomView> response = new ApiResponse<>();
        try {
            boolean a=userPointService.addUser(request.getUsername());
            BattleRoomView battle = roomService.joinRoom(
                    request.getRoomCode(),
                    request.getPlayerId(),
                    request.getUsername()
            );
            response.setData(battle);
            response.setMessage("success");
            response.setStatus(true);
        }catch (Exception e){
               response.setMessage(e.getMessage());
               response.setStatus(false);
        }
        return response;
    }

    @GetMapping("/roomstatus/{roomCode}")
    public ApiResponse<BattleRoomView> roomstatus(@PathVariable String roomCode) {
        ApiResponse<BattleRoomView> response = new ApiResponse<>();
        try {
            BattleRoomView battle = roomService.getRoomView(roomCode);
            response.setData(battle);
            response.setMessage("success");
            response.setStatus(true);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(false);
        }
        return response;
    }

    @GetMapping("/history")
    public ApiResponse<List<BattleHistoryDTO>> getbattleHistory(@RequestParam String hostUsername) {
        ApiResponse<List<BattleHistoryDTO>> response = new ApiResponse<>();
        try {
            List<BattleHistoryDTO> d=userPointService.getHistory(hostUsername);
            response.setData(d);
            response.setMessage("success");
            response.setStatus(true);
        }catch (Exception e){
            response.setMessage(e.getMessage());
            response.setStatus(false);
        }
        return response;
    }

}
