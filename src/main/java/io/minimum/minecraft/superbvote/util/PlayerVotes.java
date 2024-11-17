package io.minimum.minecraft.superbvote.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minimum.minecraft.superbvote.SuperbVote;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.Bukkit;

import java.util.UUID;

@Value
public class PlayerVotes {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final UUID uuid;
    private final String associatedUsername;
    private final int votes;
    private final Map<String, Date> lastVotes;
    private final Type type;

    public String getAssociatedUsername() {
        if (associatedUsername == null) {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
        return associatedUsername;
    }

    public enum Type {
        CURRENT,
        FUTURE;
    }

    public boolean hasVoteOnSameDay(String serviceName, Date voteDate) {
        return DateUtils.isSameDay(lastVotes.get(serviceName), voteDate);
    }

    public String getSerializedLastVotes() {
        try {
            return OBJECT_MAPPER.writeValueAsString(lastVotes);
        } catch (JsonProcessingException e) {
            SuperbVote.getPlugin()
                    .getLogger()
                    .severe("Could not serialize last votes to JSON: " + lastVotes + "\n" + e.getMessage());
            return "";
        }
    }

    public static HashMap<String, Date> deserializeLastVotes(String lastVotesString) {
        if (StringUtils.isBlank(lastVotesString)) {
            return new HashMap<>();
        }
        try {
            return OBJECT_MAPPER.readValue(lastVotesString, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            SuperbVote.getPlugin()
                    .getLogger()
                    .severe("Could not deserialize last votes from JSON: " + lastVotesString + "\n" + e.getMessage());
            return new HashMap<>();
        }
    }
}
