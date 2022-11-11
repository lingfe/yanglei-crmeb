package com.utils.lingfe;

import com.github.binarywang.java.emoji.EmojiConverter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 包含表情字符转换
 * @author: 零风
 * @CreateDate: 2021/11/4 14:55
 */
public class EmojiConverterUtil {

    /**
     * 过滤Emoji表情
     * @param str
     * @return
     */
    public static String filterEmoji(String str) {
        if(str.trim().isEmpty()){
            return str;
        }
        String pattern="[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
        String reStr="";
        Pattern emoji=Pattern.compile(pattern);
        Matcher emojiMatcher=emoji.matcher(str);
        str=emojiMatcher.replaceAll(reStr);
        return str;
    }

    /**
     * 转换表情字符toAlias
     * @param content
     * @return
     */
    public static String emojiNew(String content){
        EmojiConverter emojiConverter = EmojiConverter.getInstance();
        content= emojiConverter.toAlias(content);//将聊天内容进行转义
        return content;
    }

    /**
     * 转换表情字符toHtml
     * @param content
     * @return
     */
    public static String emojiNewToHtml(String content){
        EmojiConverter emojiConverter = EmojiConverter.getInstance();
        content= emojiConverter.toHtml(content);//将聊天内容进行转义
        return content;
    }

    /**
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
     * @param str
     * 待转换字符串
     * @return 转换后字符串
     * @throws UnsupportedEncodingException
     * exception
     */
    public static String emojiConvert1(String str) {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(
                        sb,
                        "[["
                                + URLEncoder.encode(matcher.group(1),
                                "UTF-8") + "]]");
            } catch(UnsupportedEncodingException e) {
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     * @param str
     * 转换后的字符串
     * @return 转换前的字符串
     * exception
     */
    public static String emojiRecovery2(String str){
        String patternString = "\\[\\[(.*?)\\]\\]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(sb,
                        URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch(UnsupportedEncodingException e) {

            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}