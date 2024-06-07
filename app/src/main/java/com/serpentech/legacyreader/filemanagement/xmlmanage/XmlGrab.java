package com.serpentech.legacyreader.filemanagement.xmlmanage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlGrab {
    public List <XmlGroup> xmlGroups;
    public List<String[]> headers;

    public List<XmlGroup> scanXMLForKeywordList(String xml, String keyword) {
        ArrayList<XmlGroup> xmlList = new ArrayList<>();
        String patternString = "<" + keyword + ".*?</" + keyword + ">";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);
        while (matcher.find()) {
            String match = matcher.group();
            int startLine = countLines(xml.substring(0, matcher.start())) + 1;
            int endLine = countLines(xml.substring(0, matcher.end()));

            xmlList.add(new XmlGroup(startLine, endLine, match));
        }
        return xmlList;
    }

    // make a method that grabs the first and last line of each measure

    public void scanXMLForMeasureStartEnd(String xml, ArrayList<int[]> measureStartEnd) {
        String patternString = "<measure.*?</measure>";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);
        while (matcher.find()) {
            String match = matcher.group();
            int startLine = countLines(xml.substring(0, matcher.start())) + 1;
            int endLine = countLines(xml.substring(0, matcher.end()));

            measureStartEnd.add(new int[] {startLine, endLine});
        }
    }

    public int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }


    /**
     * grabContents grabs the contents of an xml tag
     * @param data the string of the xml data you have
     * @param keyword the keyword that you want the contents of
     * @return the contents of the tag
     */
    public String grabContents(String data, String keyword) {
        String patternString = "<" + keyword + ".*?>(.*?)</" + keyword + ">";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    // Make a method that grabs the headers of the xml text
    // stuff that is the attribute="value" part of the xml

    /**
     * Make a method that grabs the headers of the xml text
     * stuff that is the attribute="value" part of the xml
     * @param data which is the string of the xml data you have
     * @param keyword the keyword that you want attributes from
     * @return String[2] list where the first element is the key and the second element is the value
     */
    public List<String[]> grabHeaders(String data, String keyword) {
        String patternString = "<" + keyword + "\\s+(.*?)>";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(data);
        ArrayList<String[]> headers = new ArrayList<>();

        while (matcher.find()) {
            String headerStr = matcher.group(1);
            // Split the header string on spaces, except when spaces are within quotes
            String[] headerPairs = headerStr.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            ArrayList<String[]> headerAttributes = new ArrayList<>();
            for (String headerPair : headerPairs) {
                // Split each pair on '=' and remove the quotes from the value
                String[] keyValue = headerPair.split("=");
                keyValue[1] = keyValue[1].replaceAll("^\"|\"$", "");
                headerAttributes.add(keyValue);
            }
            headers.addAll(headerAttributes);
        }
        return headers;
    }

    public class XmlGroup {
        public int startLine;
        public int endLine;
        public String contents;

        public XmlGroup (int start, int end, String stuff) {
            startLine = start;
            endLine = end;
            contents = stuff;
        }
    }

}
