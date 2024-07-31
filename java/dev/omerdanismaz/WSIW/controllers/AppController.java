package dev.omerdanismaz.WSIW.controllers;

import dev.omerdanismaz.WSIW.enums.ESessionAttributes;
import dev.omerdanismaz.WSIW.models.MovieRecordModel;
import dev.omerdanismaz.WSIW.models.SeriesRecordModel;
import dev.omerdanismaz.WSIW.services.IMovieRecordService;
import dev.omerdanismaz.WSIW.services.ISeriesRecordService;
import dev.omerdanismaz.WSIW.services.ISessionService;
import dev.omerdanismaz.WSIW.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

import static dev.omerdanismaz.WSIW.ai.OpenAI.getResponseFromOpenAI;
import static dev.omerdanismaz.WSIW.ai.OpenAI.promptGenerator;
import static dev.omerdanismaz.WSIW.functions.GeneralOperations.getLine;
import static dev.omerdanismaz.WSIW.functions.SessionOperations.*;

@Controller
@AllArgsConstructor
public class AppController
{
    private final IUserService userService;
    private final ISessionService sessionService;
    private final IMovieRecordService movieRecordService;
    private final ISeriesRecordService seriesRecordService;

    @GetMapping("/")
    public String indexGET(Model model,
                           HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("messageOne",
                "Type selection was not made.");
        model.addAttribute("messageTwo",
                "Category selection was not made.");
        model.addAttribute("messageThree",
                "Date Range selection was not made.");
        model.addAttribute("messageFour",
                "Rating Website selection was not made.");

        return "/app/app";
    }

    @PostMapping("/ask")
    public String askPOST(@RequestParam("CSRFToken") String CSRFToken,
                          @RequestParam("type") String type,
                          @RequestParam("category") String category,
                          @RequestParam("dateRange") String dateRange,
                          @RequestParam("ratingSite") String ratingSite,
                          @RequestParam(value = "additionalRequests", required = false) String additionalRequests,
                          HttpServletRequest httpServletRequest,
                          RedirectAttributes redirectAttributes)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(type.isEmpty() || category.isEmpty() || dateRange.isEmpty() || ratingSite.isEmpty())
        {
            return "redirect:/";
        }

        if(additionalRequests.length() > 200)
        {
            return "redirect:/";
        }

        List<MovieRecordModel> allRecommendMovieRecords;
        List<MovieRecordModel> allDoNotRecommendMovieRecords;
        List<SeriesRecordModel> allRecommendSeriesRecords;
        List<SeriesRecordModel> allDoNotRecommendSeriesRecords;

        if(type.equals("Movie"))
        {
            allRecommendMovieRecords = movieRecordService.readAllRecommendMovieRecordsByUserId
                    (Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID)));

            allDoNotRecommendMovieRecords = movieRecordService.readAllDoNotRecommendMovieRecordsByUserId
                    (Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID)));

            String allWatchedMovies;
            String allWatchedRecommendMovies;
            String allWatchedDoNotRecommendMovies;

            allWatchedRecommendMovies = allRecommendMovieRecords
                    .stream()
                    .map(MovieRecordModel::getDbfMovieRecordMovieName)
                    .collect(Collectors.joining(","));

            allWatchedDoNotRecommendMovies = allDoNotRecommendMovieRecords
                    .stream()
                    .map(MovieRecordModel::getDbfMovieRecordMovieName)
                    .collect(Collectors.joining(","));

            allWatchedMovies = allWatchedRecommendMovies + "," + allWatchedDoNotRecommendMovies;

            String prompt = promptGenerator
                    (type, category, dateRange, ratingSite, allWatchedMovies, additionalRequests);

            String response = getResponseFromOpenAI(prompt);

            redirectAttributes.addFlashAttribute("type", type);
            redirectAttributes.addFlashAttribute("response", response);
            return "redirect:/recommendation";
        }

        if(type.equals("Series"))
        {
            allRecommendSeriesRecords = seriesRecordService.readAllRecommendSeriesRecordsByUserId
                    (Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID)));

            allDoNotRecommendSeriesRecords = seriesRecordService.readAllDoNotRecommendSeriesRecordsByUserId
                    (Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID)));

            String allWatchedSeries;
            String allWatchedRecommendSeries;
            String allWatchedDoNotRecommendSeries;

            allWatchedRecommendSeries = allRecommendSeriesRecords
                    .stream()
                    .map(SeriesRecordModel::getDbfSeriesRecordSeriesName)
                    .collect(Collectors.joining(","));

            allWatchedDoNotRecommendSeries = allDoNotRecommendSeriesRecords
                    .stream()
                    .map(SeriesRecordModel::getDbfSeriesRecordSeriesName)
                    .collect(Collectors.joining(","));

            allWatchedSeries = allWatchedRecommendSeries + "," + allWatchedDoNotRecommendSeries;

            String prompt = promptGenerator
                    (type, category, dateRange, ratingSite, allWatchedSeries, additionalRequests);

            String response = getResponseFromOpenAI(prompt);

            redirectAttributes.addFlashAttribute("type", type);
            redirectAttributes.addFlashAttribute("response", response);
            return "redirect:/recommendation";
        }

        return "redirect:/";
    }

    @GetMapping("/recommendation")
    public String recommendationGET(Model model,
                                    @ModelAttribute("type") String type,
                                    @ModelAttribute("response") String response,
                                    HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        if(type.isEmpty() || response.isEmpty())
        {
            return "redirect:/";
        }

        String mediaName = getLine(response, 1);
        String mediaReleaseDate = getLine(response, 2);
        String mediaRatingWebsites = getLine(response, 3);
        String mediaSummary = getLine(response, 4);

        model.addAttribute("CSRFToken", getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("type", type);
        model.addAttribute("mediaName", mediaName);
        model.addAttribute("mediaReleaseDate", mediaReleaseDate);
        model.addAttribute("mediaRatingWebsites", mediaRatingWebsites);
        model.addAttribute("mediaSummary", mediaSummary);

        return "/app/recommendation";
    }

    @PostMapping("/recommendation")
    public String recommendationPOST(@RequestParam("CSRFToken") String CSRFToken,
                                     @RequestParam("type") String type,
                                     @RequestParam("mediaName") String mediaName,
                                     @RequestParam("buttonAction") String buttonAction,
                                     HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(type.isEmpty() || mediaName.isEmpty() || buttonAction.isEmpty())
        {
            return "redirect:/";
        }

        if(type.equals("Movie"))
        {
            Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
            boolean doNotRecommendChoice = buttonAction.equals("doNotRecommendTrue");

            MovieRecordModel movieRecordModel = MovieRecordModel
                    .builder()
                    .dbfMovieRecordUserId(userId)
                    .dbfMovieRecordMovieName(mediaName)
                    .dbfMovieRecordDoNotRecommend(doNotRecommendChoice)
                    .build();

            movieRecordService.createMovieRecord(movieRecordModel);
        }

        if(type.equals("Series"))
        {
            Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
            boolean doNotRecommendChoice = buttonAction.equals("doNotRecommendTrue");

            SeriesRecordModel seriesRecordModel = SeriesRecordModel
                    .builder()
                    .dbfSeriesRecordUserId(userId)
                    .dbfSeriesRecordSeriesName(mediaName)
                    .dbfSeriesRecordDoNotRecommend(doNotRecommendChoice)
                    .build();

            seriesRecordService.createSeriesRecord(seriesRecordModel);
        }

        return "redirect:/";
    }
}
