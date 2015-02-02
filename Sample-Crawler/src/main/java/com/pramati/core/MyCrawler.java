package com.pramati.core;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	// @Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
		// && href.startsWith("http://www.ics.uci.edu/");
				&& href.startsWith("http://www.gmail.com/");

	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	// Override
	public void visit(Page page) {

		String url = page.getWebURL().getURL();

		if (url.contains("2014")) {
			System.out.println("URL: " + url);

			/*
			 * if (page.getParseData() instanceof HtmlParseData) { HtmlParseData
			 * htmlParseData = (HtmlParseData) page.getParseData(); String text
			 * = htmlParseData.getText(); String html = htmlParseData.getHtml();
			 * Set<WebURL> links = (htmlParseData.getOutgoingUrls());
			 * 
			 * System.out.println("Text length: " + text.length());
			 * System.out.println("Html length: " + html.length());
			 * System.out.println("Number of outgoing links: " + links.size());
			 * }
			 */

			if (page.getParseData() instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page
						.getParseData();
				String text = htmlParseData.getText();
				String html = htmlParseData.getHtml();
				Set<WebURL> links = htmlParseData.getOutgoingUrls();
				Set<WebURL> linksNew = new HashSet<WebURL>();
				for (WebURL webURL : links) {

					String requiredUrl = webURL.toString();
					if (requiredUrl.contains("2014")) {
						linksNew.add(webURL);

					}
				}
				System.out.println("Text length: " + text.length());
				System.out.println("Html length: " + html.length());
				System.out.println("Number of outgoing links: "
						+ linksNew.size());

			}

			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			Set<WebURL> tr = htmlParseData.getOutgoingUrls();
			Pattern regex = Pattern.compile("[@]");
			String pageText = htmlParseData.getText();
			Matcher regexMatcher = regex.matcher(pageText);
			int i = 0;
			int width = 0;
			int subEmailCounter = 0;
			while (regexMatcher.find()) {
				if ((regexMatcher.start() - 25 > 0)
						&& (regexMatcher.end() + 25 < pageText.length())) {
					width = 25;
					String[] substr = pageText.substring(
							regexMatcher.start() - width,
							regexMatcher.end() + width).split(" ");
					for (int j = 0; j <
			  substr.length; j++) { if (substr[j].contains("@") &&
			  (substr[j].contains(".com") || substr[j] .contains(".net"))) {
			  System.out.println(substr[j]); subEmailCounter++; } }
			  
			  } else { width = 0; } }
			 

			/*
			 * regex = Pattern.compile("[http]"); regexMatcher =
			 * regex.matcher(htmlParseData.getHtml()); int imgUrlCounter=0;
			 * for(i=0;i<tr.size();i++){
			 * if(tr.get(i).toString().contains(".jpg") ||
			 * tr.get(i).toString().contains(".jpeg") ||
			 * tr.get(i).toString().contains(".gif") ||
			 * tr.get(i).toString().contains(".bmp")){ url = new
			 * URL(tr.get(i).toString()); Image image = new
			 * ImageIcon(url).getImage(); // following code to get only large
			 * images int imgWidth = image.getWidth(null); int imgHeight =
			 * image.getHeight(null); String[] t = url.toString().split("/");
			 * if((imgWidth > 400) && (imgHeight > 400)){
			 * System.out.println(tr.get(i).toString()); imgUrlCounter++; } } }
			 */

		}
	}
}
