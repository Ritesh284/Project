package com.carrental.config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AssetGenerator {

    public static void main(String[] args) {
        File rootDir = new File(".");
        File uploadsDir = new File("uploads");
        uploadsDir.mkdirs();

        // 1. Logo (40x40 to 120x120)
        createLogoImage(new File(rootDir, "logo.png"));

        // 2. Front page background hero image (1200x800)
        createHeroBgImage(new File(rootDir, "front page logo.png"));

        // 3. App stores
        createStoreBadge(new File(rootDir, "app store.png"), "App Store", "Download on the");
        createStoreBadge(new File(rootDir, "play store logo.png"), "Google Play", "GET IT ON");

        // 4. Jeep about image
        createJeepImage(new File(rootDir, "jeep.png"));

        // 5. Reviewer avatars
        createAvatar(new File(rootDir, "people1.jpg"), "Anil Sharma", new Color(0x3B, 0x82, 0xF6));
        createAvatar(new File(rootDir, "people3.jpeg"), "Dipti Khurana", new Color(0xEC, 0x48, 0x99));
        createAvatar(new File(rootDir, "people5.jpg"), "Samay Gupta", new Color(0x10, 0xB9, 0x81));

        // 6. Car Images
        createCarImage(new File(rootDir, "Defender1.jpeg"), "LAND ROVER", "DEFENDER 110", "Luxury All-Terrain SUV", new Color(0x1E, 0x29, 0x3B), new Color(0x33, 0x41, 0x55));
        createCarImage(new File(rootDir, "BMW.jpeg"), "BMW", "BMW X7 M-SPORT", "Flagship Luxury SUV", new Color(0x0F, 0x17, 0x2A), new Color(0x1D, 0x4E, 0x89));
        createCarImage(new File(rootDir, "Mercedez.jpeg"), "MERCEDES-BENZ", "GLE 450d", "Executive Luxury SUV", new Color(0x18, 0x18, 0x1B), new Color(0x52, 0x52, 0x5B));
        createCarImage(new File(rootDir, "Volvo.jpeg"), "VOLVO", "XC90 INSCRIPTION", "Swedish Luxury SUV", new Color(0x02, 0x2C, 0x43), new Color(0x05, 0x5A, 0x8C));
        createCarImage(new File(rootDir, "Supra.jpeg"), "TOYOTA", "GR SUPRA 3.0", "Twin-Scroll Turbo Sports", new Color(0x7F, 0x1D, 0x1D), new Color(0xDC, 0x26, 0x26));
        createCarImage(new File(rootDir, "Skoda.jpeg"), "SKODA", "OCTAVIA vRS", "European Sports Sedan", new Color(0x06, 0x4E, 0x3B), new Color(0x05, 0x96, 0x69));
        createCarImage(new File(rootDir, "Audi.jpeg"), "AUDI", "RS5 COUPE", "Quattro Performance", new Color(0x1C, 0x19, 0x17), new Color(0x78, 0x71, 0x6C));
        createCarImage(new File(rootDir, "Minicupoor.jpg"), "MINI", "COOPER S COUNTRYMAN", "British Iconic Compact", new Color(0x83, 0x18, 0x43), new Color(0xBE, 0x18, 0x5D));
        createCarImage(new File(rootDir, "Lambo.jpeg"), "LAMBORGHINI", "HURACAN EVO", "V10 Exotic Supercar", new Color(0xD9, 0x77, 0x06), new Color(0xF5, 0x9E, 0x0B));
        createCarImage(new File(rootDir, "for.jpeg"), "TOYOTA", "FORTUNER 4X4", "Commanding Diesel SUV", new Color(0x27, 0x27, 0x2A), new Color(0x71, 0x71, 0x7A));
        createCarImage(new File(rootDir, "Mahindra Thar.jpeg"), "MAHINDRA", "THAR LX 4X4", "Off-Road Adventure", new Color(0x88, 0x13, 0x37), new Color(0xE1, 0x1D, 0x48));
        createCarImage(new File(rootDir, "Scorpio.jpeg"), "MAHINDRA", "SCORPIO-N Z8", "Big Daddy of SUVs", new Color(0x1E, 0x3A, 0x8A), new Color(0x25, 0x63, 0xEB));
        createCarImage(new File(rootDir, "Verna1.jpeg"), "HYUNDAI", "VERNA TURBO", "Aerodynamic Sedan", new Color(0x31, 0x2E, 0x81), new Color(0x4F, 0x46, 0xE5));
        createCarImage(new File(rootDir, "Creta.jpeg"), "HYUNDAI", "CRETA SX(O)", "Ultimate Compact SUV", new Color(0x13, 0x4E, 0x4A), new Color(0x0D, 0x94, 0x88));
        createCarImage(new File(rootDir, "swift.jpeg"), "MARUTI SUZUKI", "SWIFT ZXI+", "Peppy City Hatchback", new Color(0x7C, 0x2D, 0x12), new Color(0xEA, 0x58, 0x0C));

        System.out.println("Asset generation completed successfully!");
    }

    private static void createLogoImage(File file) {
        int w = 120, h = 120;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(0xFE, 0x5B, 0x3D), w, h, new Color(0xFF, 0xAC, 0x38));
        g.setPaint(gp);
        g.fill(new RoundRectangle2D.Float(10, 10, 100, 100, 24, 24));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Poppins", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        String text = "CR";
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = ((h - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, tx, ty);

        g.dispose();
        try { ImageIO.write(img, "png", file); } catch (IOException e) { e.printStackTrace(); }
    }

    private static void createHeroBgImage(File file) {
        int w = 1200, h = 800;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Soft gradient backdrop
        GradientPaint gp = new GradientPaint(0, 0, new Color(245, 246, 250), w, h, new Color(230, 235, 245));
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);

        // Warm subtle accent shapes on the right
        g.setColor(new Color(254, 91, 61, 30));
        g.fillOval(700, 100, 600, 600);
        g.setColor(new Color(255, 172, 56, 30));
        g.fillOval(850, 250, 450, 450);

        // Stylized luxury car silhouette on right half
        g.setColor(new Color(71, 79, 160, 220));
        g.fillRoundRect(720, 380, 420, 140, 40, 40);
        g.fillRoundRect(780, 310, 280, 100, 30, 30);

        // Wheels
        g.setColor(new Color(30, 30, 30));
        g.fillOval(770, 470, 90, 90);
        g.fillOval(990, 470, 90, 90);
        g.setColor(new Color(200, 200, 200));
        g.fillOval(795, 495, 40, 40);
        g.fillOval(1015, 495, 40, 40);

        // Headlights glow
        g.setColor(new Color(255, 230, 100, 200));
        int[] xpts = {720, 640, 640, 720};
        int[] ypts = {420, 390, 460, 440};
        g.fillPolygon(xpts, ypts, 4);

        g.dispose();
        try { ImageIO.write(img, "png", file); } catch (IOException e) { e.printStackTrace(); }
    }

    private static void createStoreBadge(File file, String storeName, String subtitle) {
        int w = 240, h = 72;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(17, 24, 39));
        g.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 16, 16));
        g.setColor(new Color(55, 65, 81));
        g.draw(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 16, 16));

        // Subtitle
        g.setColor(new Color(156, 163, 175));
        g.setFont(new Font("Poppins", Font.PLAIN, 12));
        g.drawString(subtitle.toUpperCase(), 60, 28);

        // Store Name
        g.setColor(Color.WHITE);
        g.setFont(new Font("Poppins", Font.BOLD, 20));
        g.drawString(storeName, 60, 52);

        // Icon circle
        g.setColor(new Color(254, 91, 61));
        g.fillOval(16, 18, 36, 36);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Poppins", Font.BOLD, 18));
        g.drawString("▶", 28, 42);

        g.dispose();
        try { ImageIO.write(img, "png", file); } catch (IOException e) { e.printStackTrace(); }
    }

    private static void createJeepImage(File file) {
        int w = 600, h = 400;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 243, 248), w, h, new Color(220, 228, 242));
        g.setPaint(gp);
        g.fill(new RoundRectangle2D.Float(10, 10, w - 20, h - 20, 28, 28));

        // SUV Body
        g.setColor(new Color(0xFE, 0x5B, 0x3D));
        g.fillRoundRect(80, 180, 440, 120, 30, 30);
        g.fillRoundRect(160, 110, 260, 90, 20, 20);

        // Windows
        g.setColor(new Color(0xEE, 0xEF, 0xF1));
        g.fillRoundRect(180, 125, 100, 60, 10, 10);
        g.fillRoundRect(300, 125, 100, 60, 10, 10);

        // Wheels
        g.setColor(new Color(30, 41, 59));
        g.fillOval(130, 240, 100, 100);
        g.fillOval(370, 240, 100, 100);
        g.setColor(new Color(203, 213, 225));
        g.fillOval(155, 265, 50, 50);
        g.fillOval(395, 265, 50, 50);

        // Badge Text
        g.setColor(new Color(71, 79, 160));
        g.setFont(new Font("Poppins", Font.BOLD, 22));
        g.drawString("SHREE TRAVELS FLEET", 170, 70);

        g.dispose();
        try { ImageIO.write(img, "png", file); } catch (IOException e) { e.printStackTrace(); }
    }

    private static void createAvatar(File file, String name, Color color) {
        int size = 160;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(color);
        g.fillRect(0, 0, size, size);

        // Initials
        String[] parts = name.split(" ");
        String initials = (parts.length > 1) ? ("" + parts[0].charAt(0) + parts[1].charAt(0)) : ("" + name.charAt(0));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Poppins", Font.BOLD, 54));
        FontMetrics fm = g.getFontMetrics();
        int tx = (size - fm.stringWidth(initials)) / 2;
        int ty = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(initials, tx, ty);

        g.dispose();
        try { ImageIO.write(img, file.getName().endsWith("jpg") ? "jpg" : "jpeg", file); } catch (IOException e) { e.printStackTrace(); }
    }

    private static void createCarImage(File file, String brand, String name, String tag, Color topColor, Color bottomColor) {
        int w = 600, h = 400;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Rich studio background gradient
        GradientPaint bg = new GradientPaint(0, 0, topColor, w, h, bottomColor);
        g.setPaint(bg);
        g.fillRect(0, 0, w, h);

        // Studio floor lighting effect
        g.setColor(new Color(255, 255, 255, 18));
        g.fillOval(50, 240, 500, 140);

        // Stylized Vehicle Graphic
        g.setColor(new Color(255, 255, 255, 30));
        g.fillRoundRect(80, 160, 440, 110, 35, 35);
        g.fillRoundRect(160, 105, 260, 80, 25, 25);

        // Wheels
        g.setColor(new Color(20, 20, 25));
        g.fillOval(130, 220, 85, 85);
        g.fillOval(385, 220, 85, 85);

        // Rims
        g.setColor(new Color(0xFF, 0xAC, 0x38));
        g.fillOval(152, 242, 40, 40);
        g.fillOval(407, 242, 40, 40);

        // Brand & Car Name overlay banner
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, w, 70);

        g.setColor(new Color(0xFE, 0x5B, 0x3D));
        g.setFont(new Font("Poppins", Font.BOLD, 13));
        g.drawString(brand.toUpperCase(), 30, 28);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Poppins", Font.BOLD, 22));
        g.drawString(name, 30, 54);

        g.setColor(new Color(255, 255, 255, 180));
        g.setFont(new Font("Poppins", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(tag, w - fm.stringWidth(tag) - 30, 52);

        // Bottom badge
        g.setColor(new Color(0x47, 0x4F, 0xA0, 200));
        g.fillRoundRect(30, 335, 150, 36, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Poppins", Font.BOLD, 12));
        g.drawString("VERIFIED FLEET", 50, 358);

        g.dispose();
        try { ImageIO.write(img, "jpeg", file); } catch (IOException e) { e.printStackTrace(); }
    }
}
