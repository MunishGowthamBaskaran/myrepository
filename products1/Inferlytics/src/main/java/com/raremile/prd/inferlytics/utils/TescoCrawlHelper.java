/**
 * 
 */
package com.raremile.prd.inferlytics.utils;

import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.raremile.prd.inferlytics.entity.Feed;




/**
 * @author Raremile
 */
public class TescoCrawlHelper {

    private static final Logger LOG = Logger.getLogger(TescoCrawlHelper.class);

    private static final String REVIEW_URL =
        "http://reviews.tesco.com/1235direct/<ITEM_ID>/reviews.htm?format=embedded&page=<PAGE_INDEX>";

	private static final String FORMAT = "MMMM d, yyyy";

    /**
     * Fetch reviews for each item
     * 
     * @param tescoItemIDs Map<String, Integer>
     * @param brandID int
     * @param brandName String
     * @return reviewList List<SentimentFeed>
     */
	public static List<Feed> fetchReviews(
			Map<String, Integer> tescoItemIDs) {

		List<Feed> feedList = new ArrayList<Feed>();
        int itemIndex = 1;

        Collection<String> keys = tescoItemIDs.keySet();
        for (String itemID : keys) {
			List<Feed> eachFeedList = new ArrayList<Feed>();
            final String url = REVIEW_URL.replace("<ITEM_ID>", itemID);
            try {
                int pageIndex = 1;
                int reviewIndex = 0;
                int totalReviews = tescoItemIDs.get(itemID);
                while (reviewIndex < totalReviews) {
                    // Correct URL by replacing proper page numbers and Item ID

                    String newUrl = url.replace("<PAGE_INDEX>", String.valueOf(pageIndex));
                    // Connect to web page
                    Connection.Response res = CrawlHelper.connect(newUrl);
                    int statusCode = res.statusCode();
                    // Check for 200 HTTP response
                    if (statusCode != HttpURLConnection.HTTP_OK) {
                        break;
                    }
                    Element body = res.parse().body();

                    Elements reviewSections = CrawlHelper.selectAllElement(body, "#BVSubmissionPopupContainer");
                    if (Util.isEmpty(reviewSections)) {
                        break;
                    }
                    //LOG.info("Item:" + itemID + ", Page:" + pageIndex + ", reviews:" + reviewSections.size());
                    for (Element reviewSection : reviewSections) {

						Feed feed = createFeed(reviewSection, itemID);
                        eachFeedList.add(feed);
                        reviewIndex++;
                    }
                    pageIndex++;
                    // To detect terminate condition - Last page of all reviews have a div class="BVRRRatingsOnlySummaryHeader"
                    Element terminate = CrawlHelper.selectFirstElement(body, "div.BVRRRatingsOnlySummaryHeader");
                    if (null != terminate) {
                        break;
                    }
                }
            } catch (Exception e) {
                LOG.error("Exception ", e);
            }
            LOG.info(itemIndex++ + ": ----- Total Reviews for ITEM :" + itemID + " -->" + eachFeedList.size());
            feedList.addAll(eachFeedList);
        }

        return feedList;
    }

    /**
     * Method to create Feed object out of multiple div information
     * 
     * @param reviewSection Element
     * @param itemID String
     * @param brandID int
     * @param brandName
     * @return feedObject SentimentFeed
     */
	public static Feed createFeed(final Element reviewSection,
			String itemID) {

        Element rating = CrawlHelper.selectFirstElement(reviewSection, ".BVRRReviewRatingsContainer span.BVRRNumber");

        Element date = CrawlHelper.selectFirstElement(reviewSection, ".BVRRReviewDateContainer span.BVRRValue");

        Element userName = CrawlHelper.selectFirstElement(reviewSection, "span.BVRRNickname");

        Element userLocation =
            CrawlHelper.selectFirstElement(reviewSection,
                ".BVRRContextDataContainer .BVRRUserLocationContainer span.BVRRValue");

        Element userAge =
            CrawlHelper
                .selectFirstElement(reviewSection,
                    ".BVRRContextDataContainer .BVRRContextDataValueContainer.BVRRContextDataValueAgeContainer span.BVRRValue");

        Element userGender =
            CrawlHelper
                .selectFirstElement(
                    reviewSection,
                    ".BVRRContextDataContainer .BVRRContextDataValueContainer.BVRRContextDataValueGenderContainer span.BVRRValue.BVRRContextDataValue");

        Element review =
            CrawlHelper.selectFirstElement(reviewSection,
                ".BVRRReviewDisplayStyle5Text .BVRRReviewTextContainer span.BVRRReviewText");

		Feed feed = new Feed();

        feed.setFeedData(review.text());
        feed.setUserName(userName == null ? null : userName.text());
        feed.setUserLocation(userLocation == null ? null : userLocation.text());
		feed.setAge(userAge == null ? null : userAge.text());
		feed.setGender(userGender == null ? null : userGender.text());
        try {
			feed.setFeedDate(new Timestamp(new SimpleDateFormat(FORMAT,
					Locale.ENGLISH).parse(date.text()).getTime()));
        } catch (ParseException pe) {
            feed.setFeedDate(new Timestamp(System.currentTimeMillis()));
        }

        feed.setFeedRating(rating == null ? null : rating.text());
        feed.setItemId(itemID);

        return feed;
    }

    public static void main(String[] args) {

        Map<String, Integer> abc = new HashMap<String, Integer>();
        abc.put("444-1801", 3);
		fetchReviews(abc);
    }
}
