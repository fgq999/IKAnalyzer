package org.wltea.test;

import org.junit.*;
import junit.framework.TestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.runner.RunWith;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by sam on 2017/4/26.
 */

public class IKAnalyzerTest extends TestCase{

    public IKAnalyzerTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToken() {
        System.out.println("**************************\nAbout testToken:");
        //构建IK分词器，使用smart分词模式
        Analyzer analyzer = new IKAnalyzer(true);

        //获取Lucene的TokenStream对象
        TokenStream ts = null;
        try {
            ts = analyzer.tokenStream("myfield", new StringReader("这是一个中文分词的例子，你可以直接运行它！IKAnalyer can analysis english text too"));
            //获取词元位置属性
            OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
            //获取词元文本属性
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            //获取词元文本属性
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);


            //重置TokenStream（重置StringReader）
            ts.reset();
            //迭代获取分词结果
            while (ts.incrementToken()) {
                System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | " + type.type());
            }
            //关闭TokenStream（关闭StringReader）
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放TokenStream的所有资源
            if(ts != null){
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    public void testSearch(){
        System.out.println("**************************\nAbout testSearch:");
        //Lucene Document的域名
        String fieldName = "text";
        //检索内容
        String text = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";

        //实例化IKAnalyzer分词器
        Analyzer analyzer = new IKAnalyzer(true);

        Directory directory = null;
        IndexWriter iwriter = null;
        IndexReader ireader = null;
        IndexSearcher isearcher = null;
        try {
            //建立内存索引对象
            directory = new RAMDirectory();

            //配置IndexWriterConfig
            IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
            iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            iwriter = new IndexWriter(directory , iwConfig);
            //写入索引
            Document doc = new Document();
            doc.add(new StringField("ID", "10000", Field.Store.YES));
            doc.add(new TextField(fieldName, text, Field.Store.YES));
            iwriter.addDocument(doc);
            iwriter.close();


            //搜索过程**********************************
            //实例化搜索器
            ireader = DirectoryReader.open(directory);
            isearcher = new IndexSearcher(ireader);

            String keyword = "中文分词工具包";
            //使用QueryParser查询分析器构造Query对象
            QueryParser qp = new QueryParser(fieldName,  analyzer);
            qp.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = qp.parse(keyword);
            System.out.println("Query = " + query);

            //搜索相似度最高的5条记录
            TopDocs topDocs = isearcher.search(query , 5);
            System.out.println("命中：" + topDocs.totalHits);
            //输出结果
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (int i = 0; i < topDocs.totalHits; i++){
                Document targetDoc = isearcher.doc(scoreDocs[i].doc);
                System.out.println("内容：" + targetDoc.toString());
            }

        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally{
            if(ireader != null){
                try {
                    ireader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(directory != null){
                try {
                    directory.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
