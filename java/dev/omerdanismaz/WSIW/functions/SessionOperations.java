package dev.omerdanismaz.WSIW.functions;

import dev.omerdanismaz.WSIW.enums.ECookieAttributes;
import dev.omerdanismaz.WSIW.enums.ESessionAttributes;
import dev.omerdanismaz.WSIW.enums.EUserStatus;
import dev.omerdanismaz.WSIW.models.SessionModel;
import dev.omerdanismaz.WSIW.models.UserModel;
import dev.omerdanismaz.WSIW.services.ISessionService;
import dev.omerdanismaz.WSIW.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static dev.omerdanismaz.WSIW.functions.CookieOperations.getCookie;
import static dev.omerdanismaz.WSIW.functions.CookieOperations.setCookie;
import static dev.omerdanismaz.WSIW.functions.GeneralOperations.generateRandomString;
import static dev.omerdanismaz.WSIW.functions.GeneralOperations.initializeExpirationDate;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class SessionOperations
{
    public static void initializeSession(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         IUserService userService,
                                         ISessionService sessionService)
    {
        String cookieUserId = getCookie(httpServletRequest, ECookieAttributes.USER_ID);
        String cookieSessionToken = getCookie(httpServletRequest, ECookieAttributes.SESSION_TOKEN);
        String sessionUserStatus = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS);

        SessionModel searchedSession = sessionService.readSessionByToken(cookieSessionToken);

        if(searchedSession.getDbfSessionToken().equals("EMPTY"))
        {
            invalidateSession(httpServletRequest, httpServletResponse);
        }

        if(sessionUserStatus.equals("EMPTY"))
        {
            setSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN, generateRandomString(32));
            setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS, EUserStatus.GUEST.name());
        }

        if(!cookieUserId.equals("EMPTY"))
        {
            String sessionUserId = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID);

            if(!sessionUserId.equals(cookieUserId))
            {
                if(searchedSession.getDbfSessionUserId().equals(Long.parseLong(cookieUserId)))
                {
                    UserModel searchedUser = userService.readUserById(Long.parseLong(cookieUserId));

                    setSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN,
                            generateRandomString(32));
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID,
                            searchedUser.getDbfUserId().toString());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME,
                            searchedUser.getDbfUserFirstName());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME,
                            searchedUser.getDbfUserLastName());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL,
                            searchedUser.getDbfUserEmail());
                    setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS,
                            searchedUser.getDbfUserStatus().name());
                }
            }
        }
    }

    public static void buildSession(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    ISessionService sessionService,
                                    UserModel userModel)
    {
        String sessionToken = generateRandomString(64);

        SessionModel sessionModel = SessionModel
                .builder()
                .dbfSessionUserId(userModel.getDbfUserId())
                .dbfSessionToken(sessionToken)
                .dbfSessionExpiration(initializeExpirationDate())
                .build();

        sessionService.createSession(sessionModel);

        setCookie(httpServletResponse, ECookieAttributes.USER_ID, userModel.getDbfUserId().toString());
        setCookie(httpServletResponse, ECookieAttributes.SESSION_TOKEN, sessionToken);

        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID,
                userModel.getDbfUserId().toString());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME,
                userModel.getDbfUserFirstName());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME,
                userModel.getDbfUserLastName());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL,
                userModel.getDbfUserEmail());
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS,
                userModel.getDbfUserStatus().name());
    }

    public static void invalidateSession(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse)
    {
        setCookie(httpServletResponse, ECookieAttributes.USER_ID, "EMPTY");
        setCookie(httpServletResponse, ECookieAttributes.SESSION_TOKEN, "EMPTY");

        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_FIRST_NAME, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_LAST_NAME, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_EMAIL, "EMPTY");
        setSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS, EUserStatus.GUEST.name());
    }

    public static String getSessionAttribute(HttpServletRequest httpServletRequest,
                                             ESessionAttributes sessionAttribute)
    {
        try
        {
            return httpServletRequest.getSession().getAttribute(sessionAttribute.name()).toString();
        }
        catch(Exception exception)
        {
            return "EMPTY";
        }
    }

    public static void setSessionAttribute(HttpServletRequest httpServletRequest,
                                           ESessionAttributes sessionAttribute,
                                           String sessionAttributeValue)
    {
        httpServletRequest.getSession().setAttribute(sessionAttribute.name(), sessionAttributeValue);
    }

    public static boolean isUserGuest(HttpServletRequest httpServletRequest)
    {
        String sessionUserStatus = getSessionAttribute(httpServletRequest, ESessionAttributes.USER_STATUS);
        return sessionUserStatus.equals(EUserStatus.GUEST.name());
    }

    public static boolean isCSRFTokenValid(HttpServletRequest httpServletRequest, String CSRFToken)
    {
        String sessionCSRFToken = getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN);
        return CSRFToken.equals(sessionCSRFToken);
    }
}
