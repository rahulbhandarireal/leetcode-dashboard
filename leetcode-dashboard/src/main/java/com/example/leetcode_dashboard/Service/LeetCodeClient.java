package com.example.leetcode_dashboard.Service;

import com.example.leetcode_dashboard.dto.GraphQLRequest;
import com.example.leetcode_dashboard.dto.UserStatsResponse;
import com.example.leetcode_dashboard.model.DailyQuestion;
import com.example.leetcode_dashboard.model.Student;
import com.example.leetcode_dashboard.repository.DailyQuestionRepository;
import com.example.leetcode_dashboard.repository.StudentRepository;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Service
public class LeetCodeClient {

  private final WebClient webClient;
  private final ObjectMapper objectMapper = new ObjectMapper();
  Logger log = LoggerFactory.getLogger(LeetCodeClient.class);

  @Autowired
  StudentRepository studentRepository;
  @Autowired
  DailyQuestionRepository dailyQuestionRepository;

  public LeetCodeClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Student getUserStats(String username) {

    Student student = studentRepository.findByUsername(username);
    if (student != null)
      return student;

    String query = """
        query userProfile($username: String!) {
          matchedUser(username: $username) {
            username
            submitStats {
              acSubmissionNum {
                difficulty
                count
              }
               totalSubmissionNum {
                              difficulty
                              count
               }
            }
          }
        }
        """;
    GraphQLRequest request = new GraphQLRequest(query, Map.of("username", username));
    String rawJson = webClient.post()
        .uri("/graphql")
        .bodyValue(request)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    try {
      JsonNode root = objectMapper.readTree(rawJson);

      JsonNode userNode = root.path("data").path("matchedUser");
      if (userNode.isMissingNode() || userNode == null)
        throw new RuntimeException("User not found on LeetCode");
      String user = userNode.path("username").asString();

      JsonNode statsArray = userNode.path("submitStats").path("acSubmissionNum");
      JsonNode totalsub = userNode.path("submitStats").path("totalSubmissionNum");
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

      int totalSubmit = StreamSupport.stream(totalsub.spliterator(), false)
          .filter(stat -> "All".equals(stat.path("difficulty").asText()))
          .map(stat -> stat.path("count").asInt())
          .findFirst()
          .orElse(0);

      UserStatsResponse userStatsResponse = new UserStatsResponse(user, total, easy, medium, hard, totalSubmit);
      student = userStatsResponse.getStudent();
      return studentRepository.save(student);

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse LeetCode response", e);
    }
  }

  public void getRecentSolvedProblems(String username) {
    String query = """
                  recentSubmissionList(username: $username) {
          id
          title
          titleSlug
          timestamp
          statusDisplay
          lang
        }""";
        


  }

  public DailyQuestion getProblemoftheDay() {
    LocalDate today = LocalDate.parse(LocalDate.now(ZoneOffset.UTC).toString());
    ;

    String date = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    DailyQuestion dailyQuestion = dailyQuestionRepository.findByDate(date);

    if (dailyQuestion != null)
      return dailyQuestion;

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

    try {
      JsonNode root = objectMapper.readTree(rawjson).path("data");
      date = root.path("activeDailyCodingChallengeQuestion").path("date").asString();
      JsonNode questionNode = root.path("activeDailyCodingChallengeQuestion").path("question");
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

      DailyQuestion response = new DailyQuestion(
          date,
          problemID,
          title,
          titleSlug,
          difficulty,
          content,
          totalAcceptedraw,
          totalSubmissionRaw,
          acceptanceRate,
          hint,
          topicTags);

      dailyQuestionRepository.save(response);
      return response;

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse raw json", e);
    }
  }

}
