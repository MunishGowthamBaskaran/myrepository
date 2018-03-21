/**
 * 
 */
package com.raremile.prd.inferlytics.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.raremile.prd.inferlytics.entity.Feed;






/**
 * @author Raremile
 * 
 *         This is a helper file for few connectors like zappos and tesco.
 */
public class CrawlHelper {

    private static final Logger LOG = Logger.getLogger(CrawlHelper.class);

    private static final String USER_AGENT =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21";

    private static final int TIME_OUT = 100000;

    private static final String REFERRER = "http://www.google.com";

    /** Date Formatter */
	private static final String FORMAT = "MMMM d, yyyy";

    /**
     * @param url
     * @return
     * @throws IOException
     */
    public static Connection.Response connect(String url) throws IOException {

        Connection.Response res =
            Jsoup.connect(url.trim()).userAgent(USER_AGENT).timeout(TIME_OUT).ignoreHttpErrors(true).referrer(REFERRER)
                .execute();
        return res;
    }

    public static Element selectFirstElement(Element element, String select) {

        return element.select(select).first();
    }

    public static Elements selectAllElement(Element element, String select) {

        return element.select(select);
    }

	public static List<Feed> fetchReviews(Set<String> zapposItemIDs,
			String reviewTemplate) {

        List<Feed> feedList = new ArrayList<Feed>();
        int itemIndex = 1;
        for (String itemID : zapposItemIDs) {
			List<Feed> eachFeedList = new ArrayList<Feed>();
            try {
                int pageIndex = 0;
                while (true) {
                    // Correct URL by replacing proper page numbers and Item ID
                    String url = reviewTemplate.replace("<ITEM_ID>", itemID);
                    url = url.replace("<PAGE_INDEX>", String.valueOf(pageIndex));
                    // Connect to web page
                    Connection.Response res = connect(url);
                    int statusCode = res.statusCode();

                    // Check for 200 HTTP response
                    if (statusCode != HttpURLConnection.HTTP_OK) {
                        break;
                    }

                    Document html = res.parse();
                    Element body = html.body();
                    Elements reviewSections = selectAllElement(body, "div.reviewContent");
                    if (Util.isEmpty(reviewSections)) {
                        break;
                    }
                    for (Element reviewSection : reviewSections) {
						Feed feed = createFeed(reviewSection, itemID);
                        eachFeedList.add(feed);
                    }
                    pageIndex++;
                }
            } catch (Exception e) {
                LOG.error("Exception ", e);
            }
            LOG.info(itemIndex++ + ": ----- Total Reviews for ITEM :" + itemID + " -->" + eachFeedList.size());
            feedList.addAll(eachFeedList);
        }
        return feedList;
    }

	public static Feed createFeed(Element reviewSection, String itemID) {

        Element date = selectFirstElement(reviewSection, "div.reviewDate p");
        Element author = selectFirstElement(reviewSection, "h3");
        Element location = selectFirstElement(reviewSection, "p.reviewerLocation");
        Element review = selectFirstElement(reviewSection, "p.reviewSummary");
        Element ratingElement = selectFirstElement(reviewSection, "span.stars");

		Feed feed = new Feed();

        feed.setFeedData(review.text());
        feed.setUserName(author.text());
        feed.setUserLocation(location.text());
        try {
			feed.setFeedDate(new Timestamp(new SimpleDateFormat(FORMAT,
					Locale.ENGLISH).parse(date.text()).getTime()));
		} catch (ParseException pe) {
            feed.setFeedDate(new Timestamp(System.currentTimeMillis()));
        }

        String rating = ratingElement.text();
        rating = StringUtils.removeStart(rating, "Rated:");
        rating = StringUtils.removeEnd(rating, "stars!");
        feed.setFeedRating(rating.trim());
        feed.setItemId(itemID);

        return feed;
    }
	


	public String getReviewUrl (Element data) {
		String reviewURL = null;
		Elements datachildren = data.getElementsByClass("starsAndPrime");

		for (Element datachild : datachildren) {
			if (datachild != null) {
				Elements links = datachild.getElementsByTag("a");
				reviewURL = links.first().attr("href");

			}
		}
		return reviewURL;
	}

	public String[] getProdTitle(final Element data) {
		String title = null;
		String url = null;
		String[] array = new String[2];
		Elements titleClass = data.getElementsByClass("productTitle");
		title = titleClass.first().text();
		url = titleClass.first().child(0).attr("href");

		array[0] = title;
		array[1] = url;
		return array;
	}
   
	/**
	 * Returns the images names from a single page.
	 * @param	doc contains the html document
	 * @return returns the array of imageurls 
	 */
	
	public String[] getImages(final Document doc) {

		String[] imgUrls = new String[15];
		Elements prodImage = doc.getElementsByClass("productImage");
		int i = 0;
		for (Element img : prodImage) {
			imgUrls[i++] = img.child(0).child(0).attr("src");

		}
		// TODO Auto-generated method stub
		return imgUrls;
	}

	
	public static String getProdTitleforCamera(Element data) {
		// TODO Auto-generated method stub
	/*	Elements texts=data.getElementsByClass("newaps");
		  return texts.text();*/
		Elements texts=data.getElementsByClass("productTitle");
		  return texts.get(0).text();
		
	
	}

	public static String getUrlforCamera(Element data) {
		
	/*	Elements texts=data.getElementsByClass("newaps");
		  return texts.select("a").attr("href");*/
		Elements texts=data.getElementsByClass("productTitle");
		  return texts.get(0).child(0).attr("href");
		
		
		
	}

	public static String getImgUrlforCamera(Element data) {
		
		/*Elements texts=data.getElementsByTag("img");
		   return texts.get(0).attr("src");*/
		Elements texts=data.getElementsByClass("productTitle");
		   return texts.get(0).child(0).child(0).attr("src");
		
	}

	public static String getReviewUrlforCamera(String prodId) {
	
		String url="http://www.amazon.com/product-reviews/"+prodId;
		return url;
	}
	
	
	
	


}
