package rocks.inspectit.server.influx.builder.eum;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rocks.inspectit.server.influx.constants.Series;
import rocks.inspectit.shared.all.cmr.model.PlatformIdent;
import rocks.inspectit.shared.all.cmr.service.ICachedDataService;
import rocks.inspectit.shared.all.communication.data.eum.AbstractEUMElement;
import rocks.inspectit.shared.all.communication.data.eum.PageLoadRequest;
import rocks.inspectit.shared.all.communication.data.eum.PageLoadRequest.NavigationTimings;
import rocks.inspectit.shared.all.communication.data.eum.UserSessionInfo;

/**
 * @author Jonas Kunz
 *
 */
@Component
public class PageLoadRequestPointBuilder extends AbstractEUMPointBuilder<PageLoadRequest> {

	/**
	 * {@link ICachedDataService} for resolving all needed names.
	 */
	@Autowired
	protected ICachedDataService cachedDataService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean requiresSessionMetaInfo() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean requiresPageLoadRequest() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Builder> build(UserSessionInfo sessionInfo, PageLoadRequest plr, PageLoadRequest pageLoadRequest) {

		NavigationTimings timings = pageLoadRequest.getNavigationTimings();

		Builder builder = Point.measurement(Series.EumPageLoad.NAME);
		PlatformIdent platformIdent = cachedDataService.getPlatformIdentForId(pageLoadRequest.getPlatformIdent());
		builder.tag(Series.TAG_AGENT_ID, String.valueOf(pageLoadRequest.getPlatformIdent()));
		if (null != platformIdent) {
			builder.tag(Series.TAG_AGENT_NAME, platformIdent.getAgentName());
		}
		if (sessionInfo != null) {
			builder.tag(Series.EUMBasicRequestSeries.TAG_BROWSER, sessionInfo.getBrowser());
			builder.tag(Series.EUMBasicRequestSeries.TAG_DEVICE, sessionInfo.getDevice());
			builder.tag(Series.EUMBasicRequestSeries.TAG_LANGUAGE, sessionInfo.getLanguage());
		}

		builder.tag(Series.EUMBasicRequestSeries.TAG_URL, pageLoadRequest.getUrl());

		if (pageLoadRequest.getResourceCount() != -1) {

			builder.addField(Series.EumPageLoad.FIELD_RESOURCE_COUNT, pageLoadRequest.getResourceCount());
		}

		if (timings != null) {
			long navStart = timings.getNavigationStart();
			builder.time(navStart, TimeUnit.MILLISECONDS);

			builder.addField(Series.EumPageLoad.FIELD_NAVIGATION_START, navStart);
			if (timings.getConnectEnd() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_CONNECT_END, timings.getConnectEnd() - navStart);
			}
			if (timings.getConnectStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_CONNECT_START, timings.getConnectStart() - navStart);
			}
			if (timings.getDomContentLoadedEventStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_DOM_CONTENT_LOADED_EVENT_START, timings.getDomContentLoadedEventStart() - navStart);
			}
			if (timings.getDomContentLoadedEventEnd() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_DOM_CONTENT_LOADED_EVENT_END, timings.getDomContentLoadedEventEnd() - navStart);
			}
			if (timings.getDomInteractive() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_DOM_INTERACTIVE, timings.getDomInteractive() - navStart);
			}
			if (timings.getDomLoading() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_DOM_LOADING, timings.getDomLoading() - navStart);
			}
			if (timings.getDomainLookupStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_DOMAIN_LOOKUP_START, timings.getDomainLookupStart() - navStart);
			}
			if (timings.getDomainLookupEnd() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_DOMAIN_LOOKUP_END, timings.getDomainLookupEnd() - navStart);
			}
			if (timings.getFetchStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_FETCH_START, timings.getFetchStart() - navStart);
			}
			if (timings.getLoadEventStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_LOAD_EVENT_START, timings.getLoadEventStart() - navStart);
			}
			if (timings.getLoadEventEnd() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_LOAD_EVENT_END, timings.getLoadEventEnd() - navStart);
			}
			if (timings.getRedirectStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_REDIRECT_START, timings.getRedirectStart() - navStart);
			}
			if (timings.getRedirectEnd() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_REDIRECT_END, timings.getRedirectEnd() - navStart);
			}
			if (timings.getRequestStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_REQUEST_START, timings.getRequestStart() - navStart);
			}
			if (timings.getResponseStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_RESPONSE_START, timings.getResponseStart() - navStart);
			}
			if (timings.getResponseEnd() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_RESPONSE_END, timings.getResponseEnd() - navStart);
			}
			if (timings.getUnloadEventStart() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_UNLOAD_EVENT_START, timings.getUnloadEventStart() - navStart);
			}
			if (timings.getUnloadEventEnd() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_UNLOAD_EVENT_END, timings.getUnloadEventEnd() - navStart);
			}
			if (timings.getFirstPaint() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_FIRSTPAINT, timings.getFirstPaint() - navStart);
			}
			if (timings.getSpeedIndex() != 0) {
				builder.addField(Series.EumPageLoad.FIELD_SPEEDINDEX, timings.getSpeedIndex());
			}

		} else {
			builder.time(pageLoadRequest.getTimeStamp().getTime(), TimeUnit.MILLISECONDS);
		}

		return Collections.singleton(builder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Class<? extends AbstractEUMElement>> getSupportedTypes() {
		return Collections.<Class<? extends AbstractEUMElement>> singleton(PageLoadRequest.class);
	}

}