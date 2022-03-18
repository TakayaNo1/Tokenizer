# Tokenizer
- HTML
- XML
- JSON
に対応

# 使い方
'''
HTMLTokenizer com=new HTMLTokenizer(new URL("https://www.abcd.efg/hijk/lmn/opqrstu/vwxyz.html"));
Node<HTMLElement> node=com.getRootNode();
  
String match="html/body/div[ID=main]/div[ID=abc]";
HTMLElement.search(node, match).forEach((e)->{
    HTMLElement.getContents(e).forEach(s->System.out.print(s));
});
'''
