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
public class RedditSimulation implements SocialMediaClient {

    private final Random random = new Random();

    private final String[] POSITIVE_TEMPLATES = {
            "I've been using {keyword} for about 6 months now and honestly it's been a game changer. "
                    + "The build quality is exceptional and support has been responsive every time I've reached out. "
                    + "Would definitely recommend to anyone on the fence.",
            "Hot take: {keyword} is actually underrated. I switched from a competitor last month "
                    + "and the difference is night and day. Much happier with my decision.",
            "Just wanted to share my positive experience with {keyword}. Ordered on Monday, "
                    + "arrived Wednesday, worked perfectly out of the box. Refreshing these days!",
            "{keyword} really knocked it out of the park with their latest release. "
                    + "The attention to detail is impressive and you can tell they put a lot of thought into the UX.",
            "PSA: {keyword} has amazing customer support. Had an issue, they resolved it within an hour "
                    + "and even threw in a discount for the inconvenience. That's how you build loyalty.",
            "Been a {keyword} user for years. Each iteration has been an improvement. "
                    + "Rare to see a company that consistently delivers quality like this."
    };

    private final String[] NEGATIVE_TEMPLATES = {
            "Honestly pretty disappointed with {keyword}. The product looked great in marketing "
                    + "but the reality doesn't live up to the hype. Returning mine tomorrow.",
            "Anyone else having issues with {keyword}? Third time this week it's crashed on me. "
                    + "Support just keeps telling me to restart. Getting really frustrated.",
            "Unpopular opinion maybe but {keyword} has really gone downhill since the acquisition. "
                    + "They clearly prioritize profits over user experience now.",
            "{keyword} raised their prices AGAIN without adding any new features. "
                    + "This is getting ridiculous. Already looking at alternatives.",
            "Warning to anyone considering {keyword}: the return process is a nightmare. "
                    + "Been trying for 3 weeks to get my money back. Terrible customer service.",
            "The latest {keyword} update broke half my workflow. How does a company this big "
                    + "ship something so broken? No QA testing whatsoever it seems."
    };

    private final String[] NEUTRAL_TEMPLATES = {
            "Can someone give me an honest comparison between {keyword} and its main competitors? "
                    + "Looking to make a decision this week and want to weigh all options.",
            "Just noticed {keyword} is hiring a lot of engineers. "
                    + "Wonder if they're working on something new or just scaling existing ops.",
            "{keyword} earnings report came out today. Revenue up 3%, slightly below expectations. "
                    + "Nothing too exciting or alarming. Pretty much in line with analyst estimates.",
            "Does {keyword} offer student discounts? Looking into it for a school project "
                    + "but the regular pricing is a bit steep for my budget.",
            "TIL that {keyword} was founded in a garage. Interesting origin story. "
                    + "Regardless of opinions on their current products, that's pretty cool.",
            "Switching from {keyword} to another service next month when my subscription ends. "
                    + "Not because it's bad, just want to try something different."
    };

    private final String[] SUBREDDITS = {
            "r/technology", "r/gadgets", "r/business", "r/investing",
            "r/reviews", "r/askreddit", "r/products", "r/deals"
    };

    private final String[] USERNAMES = {
            "ThrowawayReviewer", "TechSavvy2024", "HonestConsumer", "DeepDiveAnalysis",
            "CasualUser99", "ProductJunkie", "BudgetBuyer", "QualityMatters",
            "SkepticalShopper", "DetailOriented", "JustMyTwoCents", "LongTimeListener"
    };

    @Override
    public List<Post> fetchPosts(String keyword, int limit) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            String content = generatePost(keyword);
            String author = USERNAMES[random.nextInt(USERNAMES.length)];
            String subreddit = SUBREDDITS[random.nextInt(SUBREDDITS.length)];
            String postId = "rd_" + UUID.randomUUID().toString().substring(0, 12);
            LocalDateTime createdAt = LocalDateTime.now()
                    .minusMinutes(random.nextInt(360)); // within last 6 hours

            posts.add(new Post("REDDIT", postId,
                    "u/" + author + " in " + subreddit, content, createdAt));
        }
        return posts;
    }

    @Override
    public String getPlatformName() {
        return "REDDIT";
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
