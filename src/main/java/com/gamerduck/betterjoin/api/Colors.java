package com.gamerduck.betterjoin.api;


import com.hypixel.hytale.server.core.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colors {
    private static final Map<Character, Color> COLOR_MAP = new HashMap();
    private static final Pattern COLOR_PATTERN = Pattern.compile("[&§]([0-9a-fk-or])");

    public static Message formatColorCodes(String text) {
        if (text != null && !text.isEmpty()) {
            List<Message> messages = new ArrayList();
            Matcher matcher = COLOR_PATTERN.matcher(text);
            int lastIndex = 0;
            Color currentColor = Color.WHITE;
            boolean bold = false;

            boolean italic;
            String textSegment;
            Message msg;
            for(italic = false; matcher.find(); lastIndex = matcher.end()) {
                if (matcher.start() > lastIndex) {
                    textSegment = text.substring(lastIndex, matcher.start());
                    if (!textSegment.isEmpty()) {
                        msg = Message.raw(textSegment).color(currentColor);
                        if (bold) {
                            msg = msg.bold(true);
                        }

                        if (italic) {
                            msg = msg.italic(true);
                        }

                        messages.add(msg);
                    }
                }

                char colorCode = matcher.group(1).charAt(0);
                if (COLOR_MAP.containsKey(colorCode)) {
                    currentColor = COLOR_MAP.get(colorCode);
                } else if (colorCode == 'r') {
                    currentColor = Color.WHITE;
                    bold = false;
                    italic = false;
                } else if (colorCode == 'l') {
                    bold = true;
                } else if (colorCode == 'o') {
                    italic = true;
                }
            }

            if (lastIndex < text.length()) {
                textSegment = text.substring(lastIndex);
                if (!textSegment.isEmpty()) {
                    msg = Message.raw(textSegment).color(currentColor);
                    if (bold) {
                        msg = msg.bold(true);
                    }

                    if (italic) {
                        msg = msg.italic(true);
                    }

                    messages.add(msg);
                }
            }

            if (messages.isEmpty()) {
                return Message.raw("");
            } else {
                return Message.join(messages.toArray(new Message[0]));
            }
        } else {
            return Message.raw("");
        }
    }

    public static String stripColorCodes(String text) {
        return text != null && !text.isEmpty() ? COLOR_PATTERN.matcher(text).replaceAll("") : text;
    }

    static {
        COLOR_MAP.put('0', new Color(0));
        COLOR_MAP.put('1', new Color(170));
        COLOR_MAP.put('2', new Color(43520));
        COLOR_MAP.put('3', new Color(43690));
        COLOR_MAP.put('4', new Color(11141120));
        COLOR_MAP.put('5', new Color(11141290));
        COLOR_MAP.put('6', new Color(16755200));
        COLOR_MAP.put('7', new Color(11184810));
        COLOR_MAP.put('8', new Color(5592405));
        COLOR_MAP.put('9', new Color(5592575));
        COLOR_MAP.put('a', new Color(5635925));
        COLOR_MAP.put('b', new Color(5636095));
        COLOR_MAP.put('c', new Color(16733525));
        COLOR_MAP.put('d', new Color(16733695));
        COLOR_MAP.put('e', new Color(16777045));
        COLOR_MAP.put('f', new Color(16777215));
    }
}