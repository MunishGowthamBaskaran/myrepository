/**
 * 
 */
package com.raremile.prd.inferlytics.connectors;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.Feed;
import com.raremile.prd.inferlytics.utils.CrawlHelper;
import com.raremile.prd.inferlytics.utils.TescoCrawlHelper;




/**
 * @author Raremile
 */
public class TescoWashingMachines {

    /** LOGGER */
    private static final Logger LOG = Logger.getLogger(TescoWashingMachines.class);

    /** Maximum No of Pages */
    private static final int MAX_ITEMS = 100;

    /** Main URL to be scanned */
    private static String urlTemplate =
        "http://www.tesco.com/direct/home-garden/washing-machines/cat15660024.cat?View=list&Items=<MAX_ITEMS>&catId=4294837573";


	private static final String brandName = "tesco";

	private static final String productName = "WashingMashine";

	private static final String sourceName = "tesco";

    /**
     * @param args
     */
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        // Get all Items from URL (along with reviews count)
        Map<String, Integer> tescoMachinesItemIDs = getItemIDs();
		LOG.info("Total Items found:" + tescoMachinesItemIDs.size());
        // Get all reviews for each Item
		List<Feed> feedList = TescoCrawlHelper
				.fetchReviews(tescoMachinesItemIDs);
		LOG.info("Total Reviews for all Items:" + feedList.size());

        long endTime = System.currentTimeMillis();

		LOG.info("Total Time taken:"
            + String.format(
                "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)),
                TimeUnit.MILLISECONDS.toSeconds((endTime - startTime))
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((endTime - startTime)))));

		insertToDB(feedList);
    }

	private static void insertToDB(List<Feed> feedList) {
		DAOFactory
				.getInstance(ApplicationConstants.LEXICONDB_PROPERTIES_FILE)
				.getSentimentDAO()
				.storePostsIntoStaging(feedList, brandName, productName,
						sourceName);
	}
    private static Map<String, Integer> getItemIDs() {

        Map<String, Integer> tescoMachinesItemIDs = new HashMap<String, Integer>();
        try {
            String url = urlTemplate.replace("<MAX_ITEMS>", String.valueOf(MAX_ITEMS));
            LOG.info("Connecting to:" + url);
            // Connect to web page
            Connection.Response res = CrawlHelper.connect(url);
            // Check for 200 HTTP response
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                Element body = res.parse().body();
                Elements indItem = CrawlHelper.selectAllElement(body, "ul li.product-bb");
                for (Element item : indItem) {
                    int count = 0;
                    String itemId = StringUtils.substringBefore(item.attr("data-tileid"), ":");
                    Element reviewCount = CrawlHelper.selectFirstElement(item, ".user-start-review a.user-reviews");
                    if (null != reviewCount) {
                        String countText = StringUtils.strip(reviewCount.text().trim(), "()");
                        count = Integer.parseInt(countText);
                    }
                    tescoMachinesItemIDs.put(itemId, count);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception ", e);
        }

        return tescoMachinesItemIDs;
    }

}
