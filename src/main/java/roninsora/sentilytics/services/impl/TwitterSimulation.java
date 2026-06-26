package roninsora.sentilytics.services.impl;

import org.springframework.stereotype.Component;
import roninsora.sentilytics.models.Post;
import roninsora.sentilytics.services.SocialMediaClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class TwitterSimulation implements SocialMediaClient {

    private final Random random = new Random();

    private final String[] POSITIVE_TEMPLATES = {
            "Just tried {keyword} and I'm absolutely blown away! Best experience ever \ud83d\udd25",
            "Honestly, {keyword} keeps getting better and better. Huge fan! \ud83d\udcaf",
            "{keyword} just announced something amazing. So excited for what's coming! \ud83d\ude80",
            "Can't believe how good {keyword} is. Worth every penny. Highly recommend!",
            "Shoutout to {keyword} for the incredible customer service today. A+ experience!",
            "The new {keyword} update is fantastic! They really listened to user feedback.",
            "{keyword} is leading the industry right now. Innovation at its finest! \u2728",
            "My {keyword} experience has been nothing but positive. Truly outstanding product.",
            "Everyone's sleeping on {keyword}. This is genuinely the best in its class.",
            "Love what {keyword} is doing. They're really making a difference! \u2764\ufe0f"
    };

    private final String[] NEGATIVE_TEMPLATES = {
            "{keyword} really dropped the ball this time. Extremely disappointed \ud83d\ude24",
            "Avoid {keyword} at all costs. Worst purchase I've made all year.",
            "Why is {keyword} so buggy lately? It crashes every time I try to use it.",
            "Customer support from {keyword} is non-existent. Been waiting days for a reply.",
            "Honestly, {keyword} used to be good, but the quality has gone way down.",
            "Just canceled my {keyword} subscription. Not worth the money anymore.",
            "Is it just me, or did {keyword} get significantly worse after the update?",
            "Do not trust {keyword}. They completely messed up my order and won't fix it.",
            "Very frustrated with {keyword}. Nothing seems to work as advertised.",
            "Save your money. {keyword} is a total scam. Complete waste of time."
    };

    private final String[] NEUTRAL_TEMPLATES = {
            "Has anyone tried the new feature on {keyword}? Thinking about getting it.",
            "I'm on the fence about {keyword}. Can someone tell me if it's worth it?",
            "Saw an ad for {keyword} today. Looks interesting but I need to do more research.",
            "What are the main differences between {keyword} and its competitors?",
            "Just reading some reviews about {keyword}. People seem very divided on it.",
            "I use {keyword} occasionally. It gets the job done but nothing special.",
            "Does {keyword} have a free trial? Want to test it before committing.",
            "I've been tracking {keyword} stock. Interesting movement this week.",
            "Here's a detailed comparison of {keyword} and the alternatives.",
            "I don't have strong feelings about {keyword}. It's just a tool I use."
    };

    private final String[] USERNAMES = {
            "TechGuru", "ConsumerReports", "DailyReview", "GadgetFan",
            "HonestOpinions", "AverageUser", "TechNewsWeekly", "EarlyAdopter"
    };

    @Override
    public List<Post> fetchPosts(String keyword, int limit) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            String content = generatePost(keyword);
            String author = "@" + USERNAMES[random.nextInt(USERNAMES.length)] + random.nextInt(100);
            String postId = "tw_" + UUID.randomUUID().toString().substring(0, 12);
            LocalDateTime createdAt = LocalDateTime.now()
                    .minusMinutes(random.nextInt(360)); // within last 6 hours

            posts.add(new Post("TWITTER", postId, author, content, createdAt));
        }
        return posts;
    }

    @Override
    public String getPlatformName() {
        return "TWITTER";
    }

    private String generatePost(String keyword) {
        int roll = random.nextInt(100);
        String[] templates;
        if (roll < 35) {
            templates = POSITIVE_TEMPLATES;
        } else if (roll < 70) {
            templates = NEGATIVE_TEMPLATES;
        } else {
            templates = NEUTRAL_TEMPLATES;
        }
        String template = templates[random.nextInt(templates.length)];
        return template.replace("{keyword}", keyword);
    }
}
