package metube.web.filters;

import metube.domain.models.binding.Bindable;
import metube.web.WebConstants;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class FilterUtils {

    private static final Logger LOG = Logger.getLogger(FilterUtils.class.getName());
    private static final String YOUTUBE_VIDEO_ID_REGEX = "(?<=[/=]|^)(?<id>[a-zA-Z0-9-]{11})(?=$|[&?])";
    private static final Pattern YOUTUBE_VIDEO_ID_PATTERN = Pattern.compile(YOUTUBE_VIDEO_ID_REGEX);

    private FilterUtils() {
    }

    static boolean isGuestUser(HttpSession session) {
        return !isAuthenticated(session);
    }

    static boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute(WebConstants.ATTRIBUTE_USERNAME) != null;
    }

    static String extractYoutubeId(String youtubeLink) {
        if (youtubeLink != null) {
            Matcher matcher = YOUTUBE_VIDEO_ID_PATTERN.matcher(youtubeLink);
            if (matcher.find()) {
                return matcher.group("id");
            }
        }
        return null;
    }

    static Optional<String> getQueryParam(String queryString, String paramName) {
        if (queryString != null && paramName != null) {
            String decoded = URLDecoder.decode(queryString, WebConstants.SERVER_ENCODING);
            String[] params = decoded.split("&");
            for (String param : params) {
                String[] kvp = param.split("=");
                if (paramName.equals(kvp[0])) {
                    return Optional.ofNullable(kvp[1]);
                }
            }
        }
        return Optional.empty();
    }

    static <T extends Bindable> T getBindingModelFromParams(ServletRequest request, Class<T> clazz) {
        try {
            request.setCharacterEncoding(WebConstants.SERVER_ENCODING_STR);
            T model = clazz.getConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                String value = request.getParameter(field.getName());
                if (value != null) {
                    String parameterStr = value.trim();
                    Object parameterValue = parseValue(field.getType(), parameterStr);
                    field.setAccessible(true);
                    field.set(model, parameterValue);
                }
            }
            return model;
        } catch (IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException |
                UnsupportedEncodingException e) {
            LOG.log(Level.SEVERE, "Failed to construct model from params: " + clazz.getName(), e);
            return null;
        }
    }

    private static Object parseValue(Class<?> type, String value) {
        if (type == null || value == null) {
            return null;
        }

        if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
            return Long.valueOf(value);
        } else if (type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
            return Float.valueOf(value);
        } else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);
        } else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);
        } else {
            return value;
        }
    }
}
