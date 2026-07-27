package com.example.leetcode_dashboard.Service;

import com.example.leetcode_dashboard.CustomException.NotFoundException;
import com.example.leetcode_dashboard.CustomException.UpstreamServiceException;
import com.example.leetcode_dashboard.component.DailyProblemHolder;
import com.example.leetcode_dashboard.dto.GraphQLRequest;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.dto.RatingDTO;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.model.SolvedProblem;
import com.example.leetcode_dashboard.model.Student;
import com.example.leetcode_dashboard.repository.KnowsRelationRepository;
import com.example.leetcode_dashboard.repository.LeetCodeProblemRepository;
import com.example.leetcode_dashboard.repository.SolvedProblemRepository;
import com.example.leetcode_dashboard.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.retry.Retry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class LeetCodeClient {

    @Autowired
    private WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    Logger log = LoggerFactory.getLogger(LeetCodeClient.class);

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    LeetCodeProblemRepository leetCodeProblemRepository;
    @Autowired
    DailyProblemHolder dailyProblemHolder;
    @Autowired
    private SolvedProblemRepository solvedProblemRepository;

    @Autowired
    private KnowsRelationRepository knowsRelationRepository;

    private static final Duration GRAPHQL_REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final int GRAPHQL_RETRY_COUNT = 2;


    private JsonNode executeGraphQl(GraphQLRequest request, String operationName) {
        try {
            String rawJson = webClient.post()
                    .uri("/graphql")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(GRAPHQL_REQUEST_TIMEOUT)
                    .retryWhen(Retry.backoff(GRAPHQL_RETRY_COUNT, Duration.ofSeconds(1))
                            .filter(this::isRetriableGraphQlFailure))
                    .block();

            if (rawJson == null || rawJson.isBlank()) {
                throw new UpstreamServiceException("LeetCode GraphQL operation '" + operationName + "' returned an empty response");
            }

            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode errorsNode = root.path("errors");
            if (errorsNode.isArray() && !errorsNode.isEmpty()) {
                throw new UpstreamServiceException("LeetCode GraphQL operation '" + operationName + "' failed: " + extractGraphQlErrors(errorsNode));
            }

            JsonNode dataNode = root.path("data");
            if (dataNode.isMissingNode() || dataNode.isNull()) {
                throw new UpstreamServiceException("LeetCode GraphQL operation '" + operationName + "' returned no data");
            }
            return dataNode;
        } catch (WebClientResponseException e) {
            throw new UpstreamServiceException("LeetCode API call for '" + operationName + "' failed with HTTP " + e.getStatusCode().value(), e);
        } catch (UpstreamServiceException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UpstreamServiceException("Failed to execute LeetCode GraphQL operation '" + operationName + "'", e);
        }
    }

    private boolean isRetriableGraphQlFailure(Throwable throwable) {
        if (throwable instanceof WebClientResponseException responseException) {
            return responseException.getStatusCode().is5xxServerError();
        }
        return true;
    }

    private String extractGraphQlErrors(JsonNode errorsNode) {
        List<String> messages = new ArrayList<>();
        for (JsonNode error : errorsNode) {
            messages.add(error.path("message").asText("Unknown GraphQL error"));
        }
        return String.join("; ", messages);
    }

    private JsonNode requireNode(JsonNode parentNode, String fieldName, String errorMessage) {
        JsonNode node = parentNode.path(fieldName);
        if (node.isMissingNode() || node.isNull()) {
            throw new RuntimeException(errorMessage);
        }
        return node;
    }

    private int requireInt(JsonNode parentNode, String fieldName, String errorMessage) {
        JsonNode node = requireNode(parentNode, fieldName, errorMessage);
        if (!node.isInt() && !node.isTextual()) {
            throw new RuntimeException(errorMessage);
        }
        return node.asInt();
    }

    private Tuple3<JsonNode, JsonNode, JsonNode> leetcodeapiuserstats(String username) {
        String userstatsquery = """
                         query userProfile($username: String!) {
                                     matchedUser(username: $username) {
                                       username
                                       submitStats {
                                         acSubmissionNum {
                                           difficulty
                                           count
                                         }
                                       }
                                     }
                                   }
                """;

        GraphQLRequest userstatsrequest = new GraphQLRequest(userstatsquery, Map.of("username", username));
        int year = Year.now().getValue();
        String stquery = """
                query userProfileCalendar($username: String!, $year: Int) {
                  matchedUser(username: $username) {
                    userCalendar(year: $year) {
                      streak
                      totalActiveDays
                    }
                  }
                }
                """;
        GraphQLRequest strequest = new GraphQLRequest(stquery, Map.of("username", username, "year", year));
        String rankquery = """
                query userContestRankingInfo($username: String!) {
                  userContestRanking(username: $username) {
                    attendedContestsCount
                    rating
                    globalRanking
                    totalParticipants
                    topPercentage
                    badge {
                      name
                    }
                  }
                  userContestRankingHistory(username: $username) {
                    attended
                    trendDirection
                    problemsSolved
                    totalProblems
                    finishTimeInSeconds
                    rating
                    ranking
                    contest {
                      title
                      startTime
                    }
                  }
                }
                """;
        GraphQLRequest rankrequest = new GraphQLRequest(rankquery, Map.of("username", username));
        Mono<JsonNode> statsMono = Mono.fromCallable(() -> executeGraphQl(userstatsrequest, "userProfile"));
        Mono<JsonNode> stmono = Mono.fromCallable(() -> executeGraphQl(strequest, "userProfileCalendar"));
        Mono<JsonNode> rankmono = Mono.fromCallable(() -> executeGraphQl(rankrequest, "userContestRankingInfo"));


        Tuple3<JsonNode, JsonNode, JsonNode> result =
                Mono.zip(statsMono, stmono, rankmono).block();
        return result;
    }

    public UserStatsResponse getUserStats(String username) {

        Student student = studentRepository.findByUsername(username);
        if (student != null &&
                student.getUpdatedAt() != null &&
                student.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(30))) {
            return student.getUserStatsResponse();
        }


        Tuple3<JsonNode, JsonNode, JsonNode> result = leetcodeapiuserstats(username);

        assert result != null;

        Pair<Integer, String> contestnoAndRank = getRanking(result.getT3());
        Pair<Integer, Integer> streakTotal = getStreakandTotal(result.getT2());


        try {
            JsonNode userNode = result.getT1().path("matchedUser");
            if (userNode.isNull() || userNode.isMissingNode()) {
                throw new NotFoundException("User not found on LeetCode");
            }
            JsonNode submitStats = requireNode(userNode, "submitStats", "LeetCode user stats are missing");
            JsonNode statsArray = requireNode(submitStats, "acSubmissionNum", "LeetCode submission stats are missing");

            int total = 0, easy = 0, medium = 0, hard = 0;

            for (JsonNode stat : statsArray) {
                String difficulty = stat.path("difficulty").asText();
                int count = stat.path("count").asInt();

                switch (difficulty) {
                    case "All" -> total = count;
                    case "Easy" -> easy = count;
                    case "Medium" -> medium = count;
                    case "Hard" -> hard = count;
                }
            }

            if (student == null) {
                student = new Student();
                student.setUsername(username);
            }
            student.setEasy(easy);
            student.setHard(hard);
            student.setMedium(medium);
            student.setRating(contestnoAndRank.b);
            student.setTotalContestAttended(contestnoAndRank.a);
            student.setStreak(streakTotal.a);
            student.setTotalActiveDays(streakTotal.b);
            student.setTotalSolved(total);
            student = studentRepository.save(student);
            return student.getUserStatsResponse();

        } catch (NotFoundException | UpstreamServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build user stats response for username: " + username, e);
        }
    }

    public Pair<Integer, String> getRanking(JsonNode dataNode) {
        try {
            JsonNode rankingNode = dataNode.path("userContestRanking");

            if (rankingNode.isMissingNode() || rankingNode.isNull()) {
                return new Pair<>(0, "Unrated");
            }

            int contestsAttended = rankingNode.path("attendedContestsCount").asInt(0);
            double rating = rankingNode.path("rating").asDouble(0.0);
            return new Pair<>(contestsAttended, String.format("%.2f", rating));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse contest ranking data from LeetCode", e);
        }
    }


    public Pair<Integer, Integer> getStreakandTotal(JsonNode dataNode) {

        int streak, totalActivedays;
        try {
            JsonNode calendarNode = dataNode
                    .path("matchedUser")
                    .path("userCalendar");

            if (calendarNode.isMissingNode() || calendarNode.isNull()) {
                throw new RuntimeException("LeetCode calendar data is missing");
            }

            streak = calendarNode.path("streak").asInt(0);
            totalActivedays = calendarNode.path("totalActiveDays").asInt(0);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse streak and active day data from LeetCode", e);
        }
        return new Pair<>(streak, totalActivedays);
    }

    @Transactional
    public List<QuestionTransferDTO> getRecentSolvedProblems(String username) {
        Student student = studentRepository.findByUsername(username);
        if (student == null) {
            throw new NotFoundException("User not found in LeetDecode database");
        }
        String query = """
                query recentSubmissions($username: String!) {
                    recentSubmissionList(username: $username) {
                        id
                        title
                        titleSlug
                        timestamp
                        statusDisplay
                        lang
                    }
                }
                """;

        GraphQLRequest graphQLRequest = new GraphQLRequest(query,
                Map.of("username", username));

        try {
            JsonNode dataNode = executeGraphQl(graphQLRequest, "recentSubmissions");
            JsonNode userNode = requireNode(dataNode, "recentSubmissionList", "Recent submissions are missing");
            if (userNode.isMissingNode()) throw new RuntimeException("User not found on LeetCode");

            String questiontopicquery = """
                    query questionDetails($titleSlug: String!) {
                      question(titleSlug: $titleSlug) {
                        questionFrontendId
                        title
                        titleSlug
                        difficulty
                        stats
                        hints
                        topicTags {
                          name
                        }
                      }
                    }
                    """;
            Map<Integer, LeetCodeProblem> problemById = new HashMap<>();
            Set<String> processedAcceptedSlugs = new HashSet<>();
            Set<Integer> processedAcceptedProblemIds = new HashSet<>();
            for (JsonNode submission : userNode) {
                String statusDisplay = submission.path("statusDisplay").asText();
                if (!"Accepted".equals(statusDisplay)) {
                    continue;
                }

                String titleSlug = submission.path("titleSlug").asText();
                if (!processedAcceptedSlugs.add(titleSlug)) {
                    continue;
                }

                GraphQLRequest recenreq = new GraphQLRequest(questiontopicquery,
                        Map.of("titleSlug", titleSlug));
                JsonNode node = executeGraphQl(recenreq, "questionDetails");
                JsonNode question = requireNode(node, "question", "Question details are missing for title slug: " + titleSlug);
                int problemId = requireInt(question, "questionFrontendId", "Question frontend id is missing for title slug: " + titleSlug);
                if (!processedAcceptedProblemIds.add(problemId)) {
                    continue;
                }

                LeetCodeProblem problem = problemById.get(problemId);
                if (problem == null) {
                    problem = leetCodeProblemRepository.findByProblemId(problemId);
                    if (problem == null) {
                        problem = makeQuestion(question);
                        problem = leetCodeProblemRepository.save(problem);
                    }
                }
                problemById.put(problemId, problem);
                long timestamp = submission.path("timestamp").asLong(0);
                LocalDateTime solvedAt = timestamp > 0
                        ? LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC)
                        : LocalDateTime.now();
                Optional<SolvedProblem> osp = solvedProblemRepository.findByStudent_UsernameAndProblem_ProblemId(username, problemId);

                if (osp.isEmpty()) {
                    SolvedProblem sp = new SolvedProblem();
                    sp.setProblem(problem);
                    sp.setStudent(student);
                    sp.setSolvedAt(solvedAt);
                    student.getSolvedProblems().add(sp);
                    solvedProblemRepository.save(sp);
                } else {
                    SolvedProblem sp = osp.get();
                    sp.setSolvedAt(solvedAt);
                    solvedProblemRepository.save(sp);
                }
            }
        } catch (UpstreamServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process recent solved problems for username: " + username, e);
        }
        return solvedProblemRepository.findTop6ByStudent_UsernameOrderBySolvedAtDescIdDesc(username).stream()
                .map(solvedProblem -> solvedProblem.getProblem().getQuestion())
                .toList();
    }

    public QuestionTransferDTO getProblemoftheDay() {
        String query = """
                query questionOfToday {
                  activeDailyCodingChallengeQuestion {
                    date
                    question {
                    questionFrontendId
                    }
                   }
                  }
                """;
        GraphQLRequest graphQLRequest = new GraphQLRequest(query);
        JsonNode dataNode = executeGraphQl(graphQLRequest, "questionOfToday");
        JsonNode challengeNode = requireNode(dataNode, "activeDailyCodingChallengeQuestion", "Daily coding challenge data is missing");
        JsonNode questionNode = requireNode(challengeNode, "question", "Daily question data is missing");
        int problemId = requireInt(questionNode, "questionFrontendId", "Daily question id is missing");
        LeetCodeProblem problem = leetCodeProblemRepository.findByProblemId(problemId);
        if (problem == null) {
            return getProblemoftheDay_Schedular();
        } else {
            return problem.getQuestion();
        }
    }

    public String isPOTDSolved(String username) {
        QuestionTransferDTO dailypotd = dailyProblemHolder.getCurrentProblem();
        if (dailypotd == null) {
            try {
                dailypotd = getProblemoftheDay();
                dailyProblemHolder.setCurrentProblem(dailypotd);
            } catch (UpstreamServiceException e) {
                throw new UpstreamServiceException("Failed to load the daily problem from LeetCode", e);
            }
        }
        getRecentSolvedProblems(username);
        Optional<SolvedProblem> osp = solvedProblemRepository.findByStudent_UsernameAndProblem_ProblemId(username, dailypotd.getProblemId());
        if (osp.isPresent()) {
            return "Solved";
        } else {
            return "Not Solved";
        }
    }

    public QuestionTransferDTO getProblemoftheDay_Schedular() {
        String query = """
                query questionOfToday {
                  activeDailyCodingChallengeQuestion {
                    question {
                    questionFrontendId
                      title
                      titleSlug
                      difficulty
                      stats
                      hints
                      topicTags {
                        name
                      }
                    }
                  }
                }
                """;

        GraphQLRequest request = new GraphQLRequest(query);

        JsonNode dataNode = executeGraphQl(request, "questionOfTodayScheduler");
        JsonNode challengeNode = requireNode(dataNode, "activeDailyCodingChallengeQuestion", "Daily coding challenge data is missing");
        JsonNode questionNode = requireNode(challengeNode, "question", "Daily question data is missing");
        LeetCodeProblem leetCodeProblem = makeQuestion(questionNode);
        leetCodeProblemRepository.save(leetCodeProblem);
        return leetCodeProblem.getQuestion();
    }

    public LeetCodeProblem makeQuestion(JsonNode questionNode) {
        try {
            Integer problemID = requireInt(questionNode, "questionFrontendId", "Question frontend id is missing");
            String title = requireNode(questionNode, "title", "Question title is missing").asText();
            String titleSlug = requireNode(questionNode, "titleSlug", "Question title slug is missing").asText();
            String difficulty = requireNode(questionNode, "difficulty", "Question difficulty is missing").asText();
            String statsString = requireNode(questionNode, "stats", "Question stats are missing").asText();
            JsonNode statsNode = objectMapper.readTree(statsString);
            int totalAcceptedraw = statsNode.path("totalAcceptedRaw").asInt(0);
            int totalSubmissionRaw = statsNode.path("totalSubmissionRaw").asInt(0);
            String acceptanceRate = statsNode.path("acRate").asText("N/A");
            List<String> hint = new ArrayList<>();
            JsonNode hintslist = questionNode.get("hints");
            if (hintslist != null && hintslist.isArray()) {
                hint = StreamSupport.stream(hintslist.spliterator(), false)
                        .map(JsonNode::asText)
                        .toList();
            }
            List<String> topicTags = new ArrayList<>();
            JsonNode tags = questionNode.get("topicTags");
            if (tags != null && tags.isArray()) {
                topicTags = StreamSupport.stream(tags.spliterator(), false)
                        .map(tag -> tag.path("name").asText())
                        .toList();
            }


            LeetCodeProblem response = new LeetCodeProblem();
            response.setTitleSlug(titleSlug);
            response.setTitle(title);
            response.setDifficulty(difficulty);
            response.setProblemId(problemID);
            response.setTopicTags(topicTags);
            response.setHints(hint);
            response.setAcceptanceRate(acceptanceRate);
            response.setTotalAcceptedRaw(totalAcceptedraw);
            response.setTotalSubmissionRaw(totalSubmissionRaw);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LeetCode question details", e);
        }
    }


    public QuestionTransferDTO getQuestionByID(String titleSlug) {
        String questionquery = """
                query getQuestion($titleSlug: String!) {
                  question(titleSlug: $titleSlug) {
                    questionFrontendId
                    title
                    titleSlug
                    difficulty
                    content
                    stats
                    hints
                    topicTags {
                      name
                    }
                  }
                }
                """;
        GraphQLRequest recenreq = new GraphQLRequest(
                questionquery,
                Map.of("titleSlug", titleSlug)
        );
        JsonNode dataNode = executeGraphQl(recenreq, "getQuestionByFrontendId");
        JsonNode questionNode = requireNode(dataNode, "question", "Question title is missing");
        LeetCodeProblem leetCodeProblem = makeQuestion(questionNode);
        return leetCodeProblem.getQuestion();
    }

    public List<RatingDTO> getRating(String username) {
        String query = """
                query userContestRankingInfo($username: String!) {
                  userContestRankingHistory(username: $username) {
                    attended
                    trendDirection
                    problemsSolved
                    totalProblems
                    finishTimeInSeconds
                    rating
                    ranking
                
                    contest {
                      title
                      startTime
                    }
                  }
                }
                """;

        Map<String, Object> variables = Map.of(
                "username", username
        );
        GraphQLRequest request = new GraphQLRequest(
                query,
                Map.of("username", username)
        );
        List<RatingDTO> ratings = new ArrayList<>();

        try {
            JsonNode dataNode = executeGraphQl(request, "userContestRankingInfo");
            JsonNode ratingNode = requireNode(dataNode, "userContestRankingHistory", "Recent Ratings are missing");
            for(JsonNode node : ratingNode) {
                RatingDTO ratingDTO=new RatingDTO();
                ratingDTO.setRating(node.get("rating").asText());
                ratingDTO.setProblemsSolved(node.get("problemsSolved").asInt());
                ratings.add(ratingDTO);
            }
        }catch (UpstreamServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process recent solved problems for username: " + username, e);
        }

        return ratings;

    }
}
