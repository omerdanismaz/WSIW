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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static dev.omerdanismaz.WSIW.functions.SessionOperations.*;

@Controller
@AllArgsConstructor
public class ProfileController
{
    private final IUserService userService;
    private final ISessionService sessionService;
    private final IMovieRecordService movieRecordService;
    private final ISeriesRecordService seriesRecordService;

    @GetMapping("/profile")
    public String profileGET(Model model,
                             HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        String sessionUserFirstName = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME);
        String sessionUserLastName = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME);
        String sessionUserFullName = sessionUserFirstName + " " + sessionUserLastName;
        String sessionUserEmail = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("sessionUserFullName", sessionUserFullName);
        model.addAttribute("sessionUserEmail", sessionUserEmail);

        return "/app/profile";
    }

    @GetMapping("/alreadyWatchedMovies")
    public String alreadyWatchedMoviesGET(Model model,
                                          HttpServletRequest httpServletRequest,
                                          HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
        List<MovieRecordModel> alreadyWatchedMovies = movieRecordService.readAllRecommendMovieRecordsByUserId(userId);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("alreadyWatchedMovies", alreadyWatchedMovies);

        return "/records/alreadyWatchedMovies";
    }

    @PostMapping("/removeMovie")
    public String removeMoviePOST(@RequestParam("CSRFToken") String CSRFToken,
                                  @RequestParam("movieId") Long movieId,
                                  HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        MovieRecordModel movieRecordModel = movieRecordService.readMovieRecordById(movieId);
        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(movieRecordModel.getDbfMovieRecordUserId().equals(userId))
        {
            movieRecordService.deleteMovieRecord(movieRecordModel);
        }

        if(movieRecordModel.isDbfMovieRecordDoNotRecommend())
        {
            return "redirect:/doNotRecommendMovies";
        }

        return "redirect:/alreadyWatchedMovies";
    }

    @GetMapping("/alreadyWatchedSeries")
    public String alreadyWatchedSeriesGET(Model model,
                                          HttpServletRequest httpServletRequest,
                                          HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
        List<SeriesRecordModel> alreadyWatchedSeries = seriesRecordService
                .readAllRecommendSeriesRecordsByUserId(userId);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("alreadyWatchedSeries", alreadyWatchedSeries);

        return "/records/alreadyWatchedSeries";
    }

    @PostMapping("/removeSeries")
    public String removeSeriesPOST(@RequestParam("CSRFToken") String CSRFToken,
                                   @RequestParam("seriesId") Long seriesId,
                                   HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        SeriesRecordModel seriesRecordModel = seriesRecordService.readSeriesRecordById(seriesId);
        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(seriesRecordModel.getDbfSeriesRecordUserId().equals(userId))
        {
            seriesRecordService.deleteSeriesRecord(seriesRecordModel);
        }

        if(seriesRecordModel.isDbfSeriesRecordDoNotRecommend())
        {
            return "redirect:/doNotRecommendSeries";
        }

        return "redirect:/alreadyWatchedSeries";
    }

    @GetMapping("/doNotRecommendMovies")
    public String doNotRecommendMoviesGET(Model model,
                                          HttpServletRequest httpServletRequest,
                                          HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
        List<MovieRecordModel> doNotRecommendMovies = movieRecordService
                .readAllDoNotRecommendMovieRecordsByUserId(userId);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("doNotRecommendMovies", doNotRecommendMovies);

        return "/records/doNotRecommendMovies";
    }

    @GetMapping("/doNotRecommendSeries")
    public String doNotRecommendSeriesGET(Model model,
                                          HttpServletRequest httpServletRequest,
                                          HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
        List<SeriesRecordModel> doNotRecommendSeries = seriesRecordService
                .readAllDoNotRecommendSeriesRecordsByUserId(userId);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("doNotRecommendSeries", doNotRecommendSeries);

        return "/records/doNotRecommendSeries";
    }

    @PostMapping("/terminateAllSessions")
    public String terminateAllSessionsPOST(@RequestParam("CSRFToken") String CSRFToken,
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

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));
        sessionService.deleteAllUserSessions(userId);

        return "redirect:/login";
    }
}
