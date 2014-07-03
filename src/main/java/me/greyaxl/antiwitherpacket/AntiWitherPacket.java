package me.greyaxl.antiwitherpacket;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiWitherPacket extends JavaPlugin {
    private ProtocolManager protocolManager;
    private Random random;

    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        random = new Random();
    }

    public void onEnable() {
        this.protocolManager.addPacketListener(new PacketAdapter(this, new PacketType[]{PacketType.Play.Server.NAMED_SOUND_EFFECT}) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player p = event.getPlayer();
                String soundName = (String) packet.getStrings().read(0);

                if (soundName.equals("mob.wither.spawn")) {
                    int x = ((Integer) packet.getIntegers().read(0)).intValue() / 8;
                    int z = ((Integer) packet.getIntegers().read(2)).intValue() / 8;

                    int distance = distanceBetweenPoints(x, p.getLocation().getBlockX(), z, p.getLocation().getBlockZ());

                    if (distance > 160) {
                        packet.getIntegers().write(0, 0);
                        packet.getIntegers().write(1, 0);
                        packet.getIntegers().write(2, 0);
                    }
                }
            }
        });

        this.protocolManager.addPacketListener(new PacketAdapter(this, new PacketType[]{PacketType.Play.Server.WORLD_EVENT}) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player p = event.getPlayer();
                int effectId = (Integer) packet.getIntegers().read(0);

                //wither spawn effect
                //see http://wiki.vg/Protocol#Effect
                if (effectId == 1013) {
                    int x = ((Integer) packet.getIntegers().read(2)).intValue();
                    int z = ((Integer) packet.getIntegers().read(4)).intValue();

                    int distance = distanceBetweenPoints(x, p.getLocation().getBlockX(), z, p.getLocation().getBlockZ());

                    if (distance > 160) {
                        packet.getIntegers().write(2, 0);
                        packet.getIntegers().write(4, 0);
                    }
                }
            }
        });
    }

    public int distanceBetweenPoints(int x1, int x2, int y1, int y2) {
        return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}