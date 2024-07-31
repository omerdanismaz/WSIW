package dev.omerdanismaz.WSIW.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings({"SpellCheckingInspection"})
public class OpenAI
{
    private static final String OPENAI_API_KEY = "";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public static String getResponseFromOpenAI(String prompt)
    {
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            JsonObject jsonObjectOne = new JsonObject();
            JsonObject jsonObjectTwo = new JsonObject();
            JsonArray jsonArray = new JsonArray();

            String role = "user";
            String model = "gpt-4o";
            int token = 300;

            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setBearerAuth(OPENAI_API_KEY);

            jsonObjectOne.addProperty("role", role);
            jsonObjectOne.addProperty("content", prompt);
            jsonArray.add(jsonObjectOne);

            jsonObjectTwo.addProperty("model", model);
            jsonObjectTwo.addProperty("max_tokens", token);
            jsonObjectTwo.add("messages", jsonArray);

            HttpEntity<String> httpEntity = new HttpEntity<>
                    (jsonObjectTwo.toString(), httpHeaders);
            ResponseEntity<String> responseEntity = restTemplate.exchange
                    (OPENAI_API_URL,HttpMethod.POST, httpEntity, String.class);

            JsonObject jsonResponseObject = new JsonObject();
            String jsonResponse = responseEntity.getBody();

            if(jsonResponse != null)
            {
                jsonResponseObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            }

            if(jsonResponseObject.has("choices"))
            {
                return jsonResponseObject
                        .get("choices")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("message")
                        .getAsJsonObject()
                        .get("content")
                        .getAsString();
            }
            else
            {
                System.err.println("The \"choices\" field was not found in the API response.");
                return "EMPTY";
            }
        }
        catch(Exception exception)
        {
            System.err.println("There was a problem with the API request.");
            return "EMPTY";
        }
    }

    public static String promptGenerator(String mediaType,
                                         String mediaCategory,
                                         String mediaReleasingDateRange,
                                         String ratingWebsites,
                                         String exclusionList,
                                         String additionalRequests)
    {
        return String.format("""
            // REQUEST START%n
            I want to watch a "%s" today.%n
            It may have one, several or all of the categories of "%s".%n
            It must have been released on "%s" date range.%n
            It must be rated above average on "%s" review websites.%n
            The thing I watch should never be "%s" because I have watched these before.%n
            Also, if it is about movies or series, consider these additional requests: "%s"%n
            // REQUEST END%n
            I WANT YOU TO ANSWER THIS REQUEST BY FOLLOWING THE RULES BELOW
            1-) The name of the movie or series should be written on the first line.
            2-) The release date of the movie or series should be written on the second line.
            3-) The rating scores found on review websites of the movie or series should be written on the third line.
            4-) Without giving spoilers, the brief subject of the movie or series should be written on the fourth line.
            5-) Do not add any other lines. Do not add any other answers.
            6-) I repeat, do not add any other lines, do not add any other answers.
            7-) Never put any answers in quotation marks.
            8-) I repeat, never put any answers in quotation marks.
            """, mediaType, mediaCategory, mediaReleasingDateRange, ratingWebsites, exclusionList, additionalRequests);
    }
}
