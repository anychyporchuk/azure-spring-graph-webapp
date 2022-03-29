package com.example.azurespringgraphwebapp.service;

import com.example.azurespringgraphwebapp.dto.CalendarDto;
import com.example.azurespringgraphwebapp.dto.EventDto;
import com.example.azurespringgraphwebapp.utils.DateTimeUtils;
import com.example.azurespringgraphwebapp.utils.GraphUtils;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.Calendar;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.CalendarCollectionPage;
import com.microsoft.graph.requests.CalendarCollectionRequestBuilder;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.EventCollectionRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    public List<EventDto> getEvents(OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = GraphUtils.getGraphServiceClient(graphAuthorizedClient);
        if (graphServiceClient == null) throw new NullPointerException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        String timeZone = getUserTimezone(graphAuthorizedClient);
        List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("startdatetime", ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        options.add(new QueryOption("enddatetime", ZonedDateTime.now().plusWeeks(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        options.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));
        // Sort results by start time
        options.add(new QueryOption("$orderby", "start/dateTime"));

        EventCollectionPage eventPage = graphServiceClient
                .me()
                .calendarView()
                .buildRequest(options)
                .select("id,subject,organizer,start,end,bodyPreview")
                .get();

        List<Event> allEvents = new LinkedList<Event>();

        // Create a separate list of options for the paging requests
        // paging request should not include the query parameters from the initial
        // request, but should include the headers.
        List<Option> pagingOptions = new LinkedList<Option>();
        pagingOptions.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));

        while (eventPage != null) {
            allEvents.addAll(eventPage.getCurrentPage());

            EventCollectionRequestBuilder nextPage =
                    eventPage.getNextPage();

            if (nextPage == null) {
                break;
            } else {
                eventPage = nextPage
                        .buildRequest(pagingOptions)
                        .get();
            }
        }
        return allEvents.stream().map(EventService::convert).collect(Collectors.toList());
    }

    public EventDto getEvent(String eventId, OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = GraphUtils.getGraphServiceClient(graphAuthorizedClient);
        String timeZone = getUserTimezone(graphAuthorizedClient);
        List<Option> options = new LinkedList<Option>();
        options.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));
        Event event = graphServiceClient.me().events(eventId).buildRequest(options).get();
        return convert(event);
    }

    public String getUserTimezone(OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = GraphUtils.getGraphServiceClient(graphAuthorizedClient);
        if (graphServiceClient == null) throw new NullPointerException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        User me = graphServiceClient
                .me()
                .buildRequest()
                .select("mailboxSettings")
                .get();

        return me.mailboxSettings.timeZone;
    }

    public void createEvent(
            EventDto event,
            OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = GraphUtils.getGraphServiceClient(graphAuthorizedClient);
        if (graphServiceClient == null) throw new NullPointerException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        String timeZone = getUserTimezone(graphAuthorizedClient);
        Event event33 = new Event();

        // POST /me/events
        graphServiceClient
                .me()
                .events()
                .buildRequest()
                .post(convert(event, timeZone));
    }

    public void updateEvent(EventDto event, OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = GraphUtils.getGraphServiceClient(graphAuthorizedClient);
        if (graphServiceClient == null) throw new NullPointerException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        String timeZone = getUserTimezone(graphAuthorizedClient);
        graphServiceClient.me().events(event.id).buildRequest().patch(convert(event, timeZone));
    }

    public void deleteEvent(EventDto event, OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = GraphUtils.getGraphServiceClient(graphAuthorizedClient);
        if (graphServiceClient == null) throw new NullPointerException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        graphServiceClient.me().events(event.id).buildRequest().delete();
    }

    public List<CalendarDto> getCalendars(OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = GraphUtils.getGraphServiceClient(graphAuthorizedClient);
        if (graphServiceClient == null) throw new NullPointerException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");

        CalendarCollectionPage calendarPage = graphServiceClient
                .me()
                .calendars()
                .buildRequest()
                .select("id,name")
                .get();

        List<Calendar> allCalendars = new LinkedList<>();

        while (calendarPage != null) {
            allCalendars.addAll(calendarPage.getCurrentPage());

            CalendarCollectionRequestBuilder nextPage =
                    calendarPage.getNextPage();

            if (nextPage == null) {
                break;
            } else {
                calendarPage = nextPage
                        .buildRequest()
                        .get();
            }
        }
        return allCalendars.stream().map(EventService::convert).collect(Collectors.toList());
    }



    private static CalendarDto convert(Calendar calendar) {
        CalendarDto dto = new CalendarDto();
        dto.setId(calendar.id);
        dto.setName(calendar.name);
        return dto;
    }
    private static Event convert(EventDto event, String timezone) {
        Event newEvent = new Event();

        newEvent.subject = event.subject;

        newEvent.start = new DateTimeTimeZone();
        newEvent.start.dateTime = LocalDateTime.of(LocalDate.parse(event.date), LocalTime.parse(event.startTime)).toString();
        newEvent.start.timeZone = timezone;

        newEvent.end = new DateTimeTimeZone();
        newEvent.end.dateTime = LocalDateTime.of(LocalDate.parse(event.date), LocalTime.parse(event.endTime)).toString();
        newEvent.end.timeZone = timezone;

        List<String> attendees = Collections.singletonList(event.attendee);
        if (attendees != null && !attendees.isEmpty()) {
            newEvent.attendees = new LinkedList<Attendee>();

            attendees.forEach((email) -> {
                Attendee attendee = new Attendee();
                // Set each attendee as required
                attendee.type = AttendeeType.REQUIRED;
                attendee.emailAddress = new EmailAddress();
                attendee.emailAddress.address = email;
                newEvent.attendees.add(attendee);
            });
        }

        if (event.description != null) {
            newEvent.body = new ItemBody();
            newEvent.body.content = event.description;
            // Treat body as plain text
            newEvent.body.contentType = BodyType.TEXT;
        }

        return newEvent;
    }

    private static EventDto convert(Event event) {
        EventDto dto = new EventDto();
        dto.id = event.id;
        dto.subject = event.subject;
        dto.description = event.bodyPreview;
        dto.organizer = event.organizer.emailAddress.name;
        if(!CollectionUtils.isEmpty(event.attendees)) {
            dto.attendee = event.attendees.stream().findFirst().get().emailAddress.address;
        }
        dto.date = DateTimeUtils.getDateFromDateTimeZone(event.start);
        dto.startTime = DateTimeUtils.getTimeFromDateTimeZone(event.start);
        dto.endTime = DateTimeUtils.getTimeFromDateTimeZone(event.end);
        return dto;
    }


}
