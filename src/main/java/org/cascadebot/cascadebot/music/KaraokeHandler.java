/*
 * Copyright (c) 2019 CascadeBot. All rights reserved.
 * Licensed under the MIT license.
 */

package org.cascadebot.cascadebot.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.cascadebot.cascadebot.CascadeBot;
import org.cascadebot.cascadebot.MDCException;
import org.cascadebot.cascadebot.data.language.Language;
import org.cascadebot.cascadebot.messaging.MessageType;
import org.cascadebot.cascadebot.messaging.Messaging;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KaraokeHandler {

    private static Map<Long, Boolean> karaokeEnabled = new HashMap<>();

    public static boolean isKaraoke(Long guildId) {
        if (karaokeEnabled.get(guildId) == null) {
            return false;
        } else {
            return karaokeEnabled.get(guildId);
        }
    }

    public static void setKaraoke(Long guildId, Boolean status) {
        karaokeEnabled.put(guildId, status);
    }

    public static void getSongLyrics(String trackId, TextChannel channel, Long guildId, Message message) throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Request request = new Request.Builder().url("https://video.google.com/timedtext?lang=en&v=" + trackId).build();
        CascadeBot.INS.getHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Messaging.sendExceptionMessage(channel, Language.i18n(channel.getGuild().getIdLong(), ""), e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.body() == null || response.code() != 200) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "commands.karaoke.cannot_find"));
                    return;
                }

                String body = "";
                try {
                    body = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (body.equals("")) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "commands.karaoke.cannot_find"));
                    return;
                }

                try {
                    Document doc = builder.parse(new StringInputStream(body));
                    NodeList texts = doc.getElementsByTagName("text");
                    Captions captions = new Captions();
                    for (int i = 0; i < texts.getLength(); i++) {
                        NamedNodeMap attrs = texts.item(i).getAttributes();
                        Node dur = attrs.getNamedItem("dur");
                        Node start = attrs.getNamedItem("start");

                        if (dur != null && start != null) {
                            captions.addCaption(StringEscapeUtils.unescapeHtml(texts.item(i).getTextContent()), Double.parseDouble(start.getNodeValue()), Double.parseDouble(dur.getNodeValue()));
                        }
                    }

                    while (isKaraoke(guildId)) {
                        List<String>caption = captions.getCaptions((CascadeBot.INS.getMusicHandler().getPlayer(guildId).getPlayer().getPlayingTrack().getPosition() / 1000D));
                        EmbedBuilder embed = new EmbedBuilder()
                                .setDescription(String.join("\n", caption))
                                .setColor(MessageType.INFO.getColor());
                        message.editMessage(embed.build()).override(true).queue();
                        Thread.sleep(14000);
                    }
                } catch (IOException | SAXException | MDCException | InterruptedException e) {
                    Messaging.sendDangerMessage(channel, Language.i18n(channel.getGuild().getIdLong(), "commands.karaoke.cannot_find"));
                    CascadeBot.LOGGER.error("Error in karaoke handler", e);
                }
            }
        });
    }

}
