package com.sinmin.neo4j.rest;

import com.sinmin.neo4j.beans.DivainaArticleBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dimuthuupeksha on 6/20/14.
 */
public class DivainaDataFetcher {
    GraphJavaClient client = new GraphJavaClient();
    public static void main(String args[]) {

        //client.addWordToGraph("Heloooo");
        System.out.println("Initializing 1");
        DivainaDataFetcher fetcher = new DivainaDataFetcher();
        System.out.println("Initializing 2");
        fetcher.readFile();
        System.out.println("Initializing 3");
    }


    private void readFile() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            System.out.println("Reading file");
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("/Users/dimuthuupeksha/Desktop/chamila/SinMinData/Divaina/Small/S14.xml"));
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            System.out.println("Adding data to graph");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node instanceof Element) {
                    NodeList metadata = node.getChildNodes();
                    //System.out.println(i + " Element");
                    String day="",month="",year="",topic="",author="";
                    List<Long> sentenceIds = new ArrayList<>();
                    for (int j = 0; j < metadata.getLength(); j++) {
                        Node attr = metadata.item(j);

                        if (attr instanceof Element) {
                            Node lastChild = attr.getLastChild();
                            String content = "";
                            if (lastChild != null) {
                                content = lastChild.getTextContent().trim();
                            }
                            switch(attr.getNodeName()){
                                case "content":

                                    String sents[] = splitToSentences(content);
                                    System.out.println(sents.length);
                                    for(int w=0;w<sents.length;w++){
                                        System.out.println(sents[w].trim());
                                        String words[] = splitToWords(sents[w].trim());
                                        Long id = client.addSentenceToGraph(words);
                                        sentenceIds.add(id);
                                        //for(String word:words){
                                          //  client.addWordToGraph(word);
                                           // System.out.println(word);
                                        //}
                                    }
                                    break;
                                case "date":
                                    //System.out.println("Date "+content);
                                    String dates[] = fetchDate(content);
                                    if(dates!=null){
                                        day = dates[0];
                                        month = dates[1];
                                        year = dates[2];
                                    }
                                    break;
                                case "topic":
                                    topic = content;
                                    break;
                                case "author":
                                    author = content;
                                    break;

                            }
                            //System.out.println(attr.getNodeName() + " - " + content);

                        }





                    }
                    DivainaArticleBean bean = new DivainaArticleBean();
                    bean.year = year;
                    bean.month = month;
                    bean.day = day;
                    bean.author = author;
                    bean.topic = topic;
                    bean.sentenceIds = sentenceIds;
                    Long articleId = client.addArticleToGraph(bean);
                    System.out.println("Year "+year);
                    System.out.println("Month "+month);
                    System.out.println("Day "+day);
                    System.out.println("Author " + author);
                    System.out.println("Topic " + topic);
                    System.out.println("Article id " + articleId);
                    for(int j=0;j<sentenceIds.size();j++){
                        System.out.println(sentenceIds.get(j));
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] splitToSentences(String article){

        return article.split("[\u002E]");
    }

    private String[] splitToWords(String sentence){
        return sentence.split("[\u0020]");
    }

    private String[] fetchDate(String dateString){
        String date[] = dateString.split("/");
        if(date!=null&&date.length==3){
            return date;
        }else{
            return null;
        }
    }
 }
