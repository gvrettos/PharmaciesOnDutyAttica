import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/*
    Script to get a set of all the available entity.Pharmacy Ids.
    And save them to file: pharmacyIds.txt .
 */

public class GetAllAvailablePharmacyIds {
    public static void getAllAvailablePharmacyIds() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

        var setOfIds = new HashSet<String>();

        final var url = "http://www.fsa.gr/duties.asp";

        WebClient webClient;
        HtmlPage page;
        HtmlInput input;
        List<HtmlAnchor> anchors;
        List<HtmlPage> pages = new ArrayList<>();

        Document jsoupdoc;
        int numOfPages;

        HtmlSelect select;
        HtmlOption option;

        List<String> pharmacyLinksJs;

        int getPositionOfSecondEqualsChar;
        int getPositionOfAndSymbolChar;
        String pharmacyId;

        try {
            webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            // Load the page

            page = webClient.getPage(url);

            // Get ids from all dates

            // If we won't include yesterday, we should also not include today.
            var stringDates = getLastStringDatesFromYesterday(36);

            System.out.println(Arrays.toString(stringDates.toArray()));

            for (var date : stringDates) {
                System.out.println(date);
                select = page.getForms().get(0).getSelectByName("dateduty");
                option = select.getOptionByValue(date);
                select.setSelectedAttribute(option, true);

                input = page.getForms().get(0).getInputsByValue("").get(1);

                // Click Search

                page = input.click();

                // jsoup Code

                jsoupdoc = Jsoup.parse(page.asXml());
                var numOfPagesAsText = jsoupdoc.select("html body table tbody tr td:eq(1) table tbody tr:eq(4) td table tbody tr td nobr").text().trim();
                // this equals this XPath: /html/body/table/tbody/tr/td[2]/table/tbody/tr[5]/td/table/tbody/tr[1]/td/nobr


                // If there are more than one pages.
                if (!numOfPagesAsText.equals("")) {
                    numOfPages = Integer.parseInt(numOfPagesAsText.substring(numOfPagesAsText.lastIndexOf(" ") + 1));
                } else {
                    numOfPages = 1;

                }

                pages.add(page);

                // Click next until the last page.

                anchors = page.getAnchors();

                for (var i = 0; i < numOfPages - 1; i++) {
                    page = anchors.get(10).click();
                    pages.add(page);
                }
            }

            for (var singlePage : pages) {
                jsoupdoc = Jsoup.parse(singlePage.asXml());
                pharmacyLinksJs = jsoupdoc.select("html body table tbody tr td:eq(1) table tbody tr:eq(3) td table tbody tr a").eachAttr("onclick");

                for (String linkJs : pharmacyLinksJs) {
                    linkJs = linkJs.trim();
                    getPositionOfSecondEqualsChar = linkJs.indexOf("=", linkJs.indexOf("=") + 1);
                    getPositionOfAndSymbolChar = linkJs.indexOf("&", getPositionOfSecondEqualsChar);
                    pharmacyId = linkJs.substring(getPositionOfSecondEqualsChar + 1, getPositionOfAndSymbolChar);
                    setOfIds.add(pharmacyId);
                }
            }

            Files.deleteIfExists(Paths.get("pharmacyIds.txt"));
            try (FileWriter fw = new FileWriter("pharmacyIds.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                for (var id : setOfIds) {
                    out.println(id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getLastStringDatesFromYesterday(int numOfDays) {
        var stringDates = new ArrayList<String>();

        // it starts from -1 to numOfDays -2 because it starts from yesterday
        for (var i = -1; i <= numOfDays - 2; i++) {
            stringDates.add(DateHelper.dateToString(DateHelper.getDateFromTodayPlusDays(i)));
        }

        return stringDates;
    }
}
