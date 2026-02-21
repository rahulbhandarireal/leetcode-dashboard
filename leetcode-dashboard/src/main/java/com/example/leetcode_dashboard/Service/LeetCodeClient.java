package com.example.leetcode_dashboard.Service;

import com.example.leetcode_dashboard.component.DailyProblemHolder;
import com.example.leetcode_dashboard.dto.GraphQLRequest;
import com.example.leetcode_dashboard.dto.QuestionTransferDTO;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.LeetCodeProblem;
import com.example.leetcode_dashboard.model.Student;
import com.example.leetcode_dashboard.repository.LeetCodeProblemRepository;
import com.example.leetcode_dashboard.repository.StudentRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class LeetCodeClient {

  private final WebClient webClient;
  private final ObjectMapper objectMapper = new ObjectMapper();
  Logger log = LoggerFactory.getLogger(LeetCodeClient.class);

  @Autowired
  StudentRepository studentRepository;

  @Autowired
  LeetCodeProblemRepository leetCodeProblemRepository;

  @Autowired
  private  DailyProblemHolder holder;

  public LeetCodeClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Student getUserStats(String username) {
      Student student  = studentRepository.findByUsername(username);
    if (student != null)
      return student;

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
      String stquery= """
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
              query userContestRating($username: String!) {
                userContestRanking(username: $username) {
                  rating
                }
              }
              """;
      GraphQLRequest rankrequest = new GraphQLRequest(rankquery, Map.of("username", username));
      Mono<String> statsMono = webClient.post()
              .uri("/graphql")
              .bodyValue(userstatsrequest)
              .retrieve()
              .bodyToMono(String.class);

      Mono<String> stmono = webClient.post()
              .uri("/graphql")
              .bodyValue(strequest)
              .retrieve()
              .bodyToMono(String.class);

      Mono<String> rankmono = webClient.post()
              .uri("/graphql")
              .bodyValue(rankrequest)
              .retrieve()
              .bodyToMono(String.class);

      Tuple3<String, String, String> result =
              Mono.zip(statsMono, stmono, rankmono).block();

      assert result != null;
      String rank=getRanking(result.getT3());
      Pair<Integer,Integer> streakTotal=getStreakandTotal(result.getT2());


      try {
      JsonNode root = objectMapper.readTree(result.getT1());

      JsonNode userNode = root.path("data").path("matchedUser");
          if (userNode.isNull() || userNode.isMissingNode()) {
              throw new RuntimeException("User not found on LeetCode");
          }
           JsonNode statsArray = userNode.path("submitStats").path("acSubmissionNum");

      int total = 0, easy = 0, medium = 0, hard = 0;

      for (JsonNode stat : statsArray) {
        String difficulty = stat.path("difficulty").asString();
        int count = stat.path("count").asInt();

        switch (difficulty) {
          case "All" -> total = count;
          case "Easy" -> easy = count;
          case "Medium" -> medium = count;
          case "Hard" -> hard = count;
        }
      }

          UserStatsResponse userStatsResponse = UserStatsResponse.builder()
              .username(username)
              .easySolved(easy)
              .hardSolved(hard)
              .mediumSolved(medium)
                  .rating(rank)
                  .streak(streakTotal.a)
                  .totalActiveDays(streakTotal.b)
              .totalSolved(total)
              .build();

      student = userStatsResponse.getStudent();
      return studentRepository.save(student);

    } catch (Exception e) {

      throw new RuntimeException("Failed to parse LeetCode response", e);
    }
  }

  public String getRanking(String rawJson) {
      double ranking ;
      try {
      JsonNode root = objectMapper.readTree(rawJson);
       ranking = root.path("data").path("userContestRanking").path("rating").asDouble(0.0);
      }catch (Exception e) {
          throw new RuntimeException("Failed to parse User rating", e);
      }
      return String.format("%.2f",ranking);
  }

  public Pair<Integer,Integer> getStreakandTotal(String rawjson){

      int streak,totalActivedays;
      try {
        JsonNode root = objectMapper.readTree(rawjson);
          JsonNode calendarNode = root
                  .path("data")
                  .path("matchedUser")
                  .path("userCalendar");

           streak = calendarNode.path("streak").asInt(0);
           totalActivedays = calendarNode.path("totalActiveDays").asInt(0);
      }catch (Exception e) {
          throw new RuntimeException("Failed to parsegetStreak and Total active days", e);
      }
      return new Pair<>(streak,totalActivedays);
  }

  public List<LeetCodeProblem> getRecentSolvedProblems(String username) {
      List<LeetCodeProblem> recentProblems=new ArrayList<>();
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

    GraphQLRequest graphQLRequest=new GraphQLRequest(query,
            Map.of("username", username));

    String rawJson = webClient.post()
            .uri("/graphql")
            .bodyValue(graphQLRequest)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    try {
      JsonNode root = objectMapper.readTree(rawJson);
      JsonNode userNode = root.path("data").path("recentSubmissionList");
      if (userNode.isMissingNode() || userNode == null) throw new RuntimeException("User not found on LeetCode");

      String questiontopicquery= """
              query questionDetails($titleSlug: String!) {
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
      for(JsonNode submission : userNode) {
        String titleSlug = submission.path("titleSlug").asString();
        GraphQLRequest recenreq=new GraphQLRequest(questiontopicquery,
                Map.of("titleSlug", titleSlug));
        String rawjson = webClient.post()
                .uri("/graphql")
                .bodyValue(recenreq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String date=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JsonNode node = objectMapper.readTree(rawjson).path("data");
        JsonNode question = node.path("question");
        Integer problemID = question.path("questionFrontendId").asInt();
        Optional<LeetCodeProblem> problem;
        problem = Optional.ofNullable(leetCodeProblemRepository.findByProblemId(problemID));

        if (problem.isPresent()) {
            continue;
        }
        LeetCodeProblem qu=  makeQuestion(question,date).getSolvedProblem();
       recentProblems.add(qu);
       leetCodeProblemRepository.save(qu);
      }




    }catch (Exception e){
      throw new RuntimeException("Failed to parse LeetCode response", e);
    }
    return recentProblems;
  }

  public QuestionTransferDTO getProblemoftheDay(){
     QuestionTransferDTO leetCodeProblem= holder.getLeetCodeProblem();

     return  leetCodeProblem;
  }

  public LeetCodeProblem getProblemoftheDay_Schedular() {
    LocalDate today = LocalDate.parse(LocalDate.now(ZoneOffset.UTC).toString());
    String date = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      String query = """
        query questionOfToday {
          activeDailyCodingChallengeQuestion {
            date
            link
            question {
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
        }
        """;

      GraphQLRequest request = new GraphQLRequest(query);

      String rawjson = webClient.post()
              .uri("/graphql")
              .bodyValue(request)
              .retrieve()
              .bodyToMono(String.class)
              .block();

      log.info(rawjson);
      JsonNode root = objectMapper.readTree(rawjson).path("data");
      date = root.path("activeDailyCodingChallengeQuestion").path("date").asString();
      JsonNode questionNode = root.path("activeDailyCodingChallengeQuestion").path("question");
      LeetCodeProblem question= makeQuestion(questionNode,date).getSolvedProblem();
      return question;
  }

  public String isPOTDSolved(String username){

      String query= """
              query recentSubmissions($username: String!) {
                recentSubmissionList(username: $username) {
                  titleSlug
                  statusDisplay
                }
              }
              """;
      GraphQLRequest request = new GraphQLRequest(query,Map.of("username",username));

      String rawjson = webClient.post()
              .uri("/graphql")
              .bodyValue(request)
              .retrieve()
              .bodyToMono(String.class)
              .block();

      JsonNode root = objectMapper.readTree(rawjson).path("data").path("recentSubmissionList");
      for(JsonNode submission : root) {
          String titleSlug = submission.path("titleSlug").asString();
          if(leetCodeProblemRepository.findByTitleSlug(titleSlug) != null){
              return "solved";
          }

      }
      return "not solved";

  }



  public QuestionTransferDTO makeQuestion(JsonNode questionNode, String date) {
    try {
     Integer problemID = questionNode.path("questionFrontendId").asInt();
      String title = questionNode.path("title").asString();
      String titleSlug = questionNode.path("titleSlug").asString();
      String difficulty = questionNode.path("difficulty").asString();
      String content = questionNode.path("content").asString();
      String statsString = questionNode.path("stats").asText();
      JsonNode statsNode = objectMapper.readTree(statsString);
      int totalAcceptedraw = statsNode.path("totalAcceptedRaw").asInt(0);
      int totalSubmissionRaw = statsNode.path("totalSubmissionRaw").asInt(0);
      String acceptanceRate = statsNode.path("acRate").asText("N/A");
      List<String> hint = new ArrayList<>();
      JsonNode hintslist = questionNode.get("hints");
      hint = StreamSupport.stream(hintslist.spliterator(), false)
              .map(JsonNode::asText)
              .toList();
      List<String> topicTags = new ArrayList<>();
      JsonNode tags = questionNode.get("topicTags");
      topicTags = StreamSupport.stream(tags.spliterator(), false)
              .map(tag -> tag.path("name").asText())
              .toList();


      QuestionTransferDTO response = new QuestionTransferDTO();
      response.setTitleSlug(titleSlug);
      response.setTitle(title);
      response.setContent(content);
      response.setDifficulty(difficulty);
      response.setProblemId(problemID);
      response.setDate(date);
      response.setTopicTags(topicTags);
      response.setHints(hint);
      response.setAcceptanceRate(acceptanceRate);
      response.setTotalAcceptedRaw(totalAcceptedraw);
      response.setTotalSubmissionRaw(totalSubmissionRaw);
      return response;

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse raw json", e);
    }
  }

}
