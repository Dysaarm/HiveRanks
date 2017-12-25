package me.heyimblake.hiveranks.runnables;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.heyimblake.hiveranks.CachedPlayerManager;
import me.heyimblake.hiveranks.util.CachedPlayer;
import me.heyimblake.hiveranks.util.HiveRank;
import me.heyimblake.hiveranks.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

/**
 * https://heyimblake.me
 *
 * @author heyimblake
 * @since 3/17/2017
 */
public class UpdateCacheRunnable extends BukkitRunnable {

    private UUID uuid;
    private HiveRank[] updatedRanks;
    private int displayRank;

    /**
     * Constructs an UpdateCacheRunnable object.
     *
     * @param uuid the uuid of the player to be updated
     */
    public UpdateCacheRunnable(UUID uuid) {
        this(uuid, -1);
    }

    /**
     * Constructs an UpdateCacheRunnable object.
     *
     * @param uuid        the uuid of the player to be updated
     * @param displayRank the rankid of the display rank
     */
    public UpdateCacheRunnable(UUID uuid, int displayRank) {
        this.uuid = uuid;
        this.displayRank = displayRank;
        updatedRanks = new HiveRank[2]; // 0: Display, 1: Hive
    }

    @Override
    public void run() {
        CachedPlayerManager.getInstance().initializeFile(uuid);

        try {
            getUpdatedRanks();
        } catch (IOException e) {
            e.printStackTrace();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                MessageUtils.sendErrorMessage(player, "An error occurred while trying to fetch your HiveMC rank. Be sure you have joined TheHive by connecting to \"play.hivemc.com\" and returning back here. See console for more details.", true);
            return;
        }

        CachedPlayerManager manager = CachedPlayerManager.getInstance();

        if (manager.isCached(uuid)) {
            manager.getCachedPlayer(uuid).update(updatedRanks[0].getIndex(), updatedRanks[1].getIndex());
            return;
        }

        new CachedPlayer(uuid, updatedRanks[0].getIndex(), updatedRanks[1].getIndex(), System.currentTimeMillis()).update(updatedRanks[0].getIndex(), updatedRanks[1].getIndex());
    }

    /**
     * Gets the updated Hive rank and sets the Display rank to a given one or the Hive one.
     *
     * @throws IOException related to URL connection
     */
    private void getUpdatedRanks() throws IOException {
        URL url = new URL("https://api.hivemc.com/v1/player/" + uuid.toString().replaceAll("-", ""));
        HttpsURLConnection request = (HttpsURLConnection) url.openConnection();
        request.setRequestProperty("Content-Type", "application/json");
        request.addRequestProperty("User-Agent", "Mozilla/5.0");
        request.connect();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject object = element.getAsJsonObject();
        JsonElement modernRank = object.get("modernRank");

        HiveRankAPIResponse response = new Gson().fromJson(modernRank, new TypeToken<HiveRankAPIResponse>() {}.getType());

        updatedRanks[1] = HiveRank.getHiveRankFromID(response.getIndex());

        if (displayRank == -1) {
            updatedRanks[0] = HiveRank.getHiveRankFromID(response.getIndex());
            request.disconnect();
            return;
        }

        updatedRanks[0] = HiveRank.getHiveRankFromID(displayRank);
        request.disconnect();
    }

    private static class HiveRankAPIResponse {

        private String enumName;
        private String human;
        private int index;

        public HiveRankAPIResponse(String enumName, String human, int index) {
            this.enumName = enumName;
            this.human = human;
            this.index = index;
        }

        public String getEnumName() {
            return enumName;
        }

        public String getHuman() {
            return human;
        }

        public int getIndex() {
            return index;
        }
    }
}
