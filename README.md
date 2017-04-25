# IKAnalyzer
ref : http://blog.csdn.net/eguid_1/article/details/51908862
注意：基于lucene5.5.4版本
一、简单介绍下IK Analyzer
IK Analyzer是linliangyi2007的作品，再此表示感谢，他的博客地址：http://linliangyi2007.iteye.com/
IK Analyzer支持两种分词，一种是最细粒度分词（推荐使用，Ik默认采用最细粒度），还有一种的智能分词（测试了一下智能分词还没有lucene自带的分词准确，呵呵了）。
二、IK Analyzer兼容性问题解决办法
IKanalyzer目前我们需要修改一下IKanalyzer的源码，让它支持lucene5.5.4版本。
