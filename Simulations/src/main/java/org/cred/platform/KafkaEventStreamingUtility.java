package org.cred.platform;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.errors.TopicExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaEventStreamingUtility {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    // Topic names
    private static final String TRANSACTION_TOPIC = "user_transactions";
    private static final String ONBOARDING_TOPIC = "user_onboarding_data";
    private static final String AFFLUENCE_TOPIC = "user_affluence_score_update";
    private static final String AT_RISK_TOPIC = "user_at_risk";
    private static final String BRAND_AFFINITY_TOPIC = "user_brand_affinity_score";

    private final KafkaProducer<String, String> producer;
    private final AdminClient adminClient;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    // Sample data arrays
    private static final String[] USER_IDS = {
            "user_001", "user_002", "user_003", "user_004", "user_005",
            "user_006", "user_007", "user_008", "user_009", "user_010"
    };

    // Topic creation method
    private void createTopicsIfNotExist() {
        try {
            System.out.println("Checking and creating Kafka topics if they don't exist...");

            // Define topics with their configurations
            List<NewTopic> topics = Arrays.asList(
                    new NewTopic(TRANSACTION_TOPIC, 3, (short) 1)
                            .configs(Map.of(
                                    "cleanup.policy", "delete",
                                    "retention.ms", "604800000", // 7 days
                                    "segment.ms", "86400000"     // 1 day
                            )),
                    new NewTopic(ONBOARDING_TOPIC, 3, (short) 1)
                            .configs(Map.of(
                                    "cleanup.policy", "delete",
                                    "retention.ms", "2592000000", // 30 days
                                    "segment.ms", "86400000"
                            )),
                    new NewTopic(AFFLUENCE_TOPIC, 3, (short) 1)
                            .configs(Map.of(
                                    "cleanup.policy", "delete",
                                    "retention.ms", "2592000000", // 30 days
                                    "segment.ms", "86400000"
                            )),
                    new NewTopic(AT_RISK_TOPIC, 3, (short) 1)
                            .configs(Map.of(
                                    "cleanup.policy", "delete",
                                    "retention.ms", "2592000000", // 30 days
                                    "segment.ms", "86400000"
                            )),
                    new NewTopic(BRAND_AFFINITY_TOPIC, 3, (short) 1)
                            .configs(Map.of(
                                    "cleanup.policy", "delete",
                                    "retention.ms", "2592000000", // 30 days
                                    "segment.ms", "86400000"
                            ))
            );

            CreateTopicsResult result = adminClient.createTopics(topics);

            // Wait for topic creation to complete
            for (Map.Entry<String, org.apache.kafka.common.KafkaFuture<Void>> entry : result.values().entrySet()) {
                try {
                    entry.getValue().get();
                    System.out.println("Topic created successfully: " + entry.getKey());
                } catch (Exception e) {
                    if (e.getCause() instanceof TopicExistsException) {
                        System.out.println("Topic already exists: " + entry.getKey());
                    } else {
                        System.err.println("Error creating topic " + entry.getKey() + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("Topic creation check completed.");

        } catch (Exception e) {
            System.err.println("Error during topic creation: " + e.getMessage());
            e.printStackTrace();
        }
    };

    private static final String[] TRANSACTION_TYPES = {
            "PURCHASE", "REFUND", "TRANSFER", "DEPOSIT", "WITHDRAWAL"
    };

    private static final String[] MERCHANTS = {
            "Amazon", "Walmart", "Target", "Starbucks", "McDonald's",
            "Apple Store", "Netflix", "Spotify", "Uber", "Airbnb"
    };

    private static final String[] CATEGORIES = {
            "FOOD_DINING", "SHOPPING", "ENTERTAINMENT", "TRANSPORTATION",
            "UTILITIES", "HEALTHCARE", "EDUCATION", "TRAVEL"
    };

    private static final String[] BRANDS = {
            "Nike", "Apple", "Samsung", "Coca-Cola", "McDonald's",
            "Starbucks", "Amazon", "Netflix", "Tesla", "Google"
    };

    private static final String[] RISK_REASONS = {
            "UNUSUAL_SPENDING_PATTERN", "MULTIPLE_FAILED_LOGINS",
            "SUSPICIOUS_LOCATION", "HIGH_TRANSACTION_VELOCITY",
            "ACCOUNT_DORMANCY", "SUSPICIOUS_MERCHANT"
    };

    private static final String[] ONBOARDING_STEPS = {
            "ACCOUNT_CREATED", "EMAIL_VERIFIED", "PHONE_VERIFIED",
            "KYC_COMPLETED", "FIRST_DEPOSIT", "PROFILE_COMPLETED"
    };

    public KafkaEventStreamingUtility() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        this.producer = new KafkaProducer<>(props);

        // Create admin client for topic management
        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        adminProps.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        adminProps.put(AdminClientConfig.RETRIES_CONFIG, 3);
        this.adminClient = AdminClient.create(adminProps);

        // Create topics if they don't exist
        createTopicsIfNotExist();
    }

    // Event Classes
    public static class Transaction {
        @JsonProperty("userId")
        public String userId;
        @JsonProperty("transactionId")
        public String transactionId;
        @JsonProperty("amount")
        public BigDecimal amount;
        @JsonProperty("currency")
        public String currency;
        @JsonProperty("transactionType")
        public String transactionType;
        @JsonProperty("merchant")
        public String merchant;
        @JsonProperty("category")
        public String category;
        @JsonProperty("timestamp")
        public String timestamp;
        @JsonProperty("description")
        public String description;
        @JsonProperty("location")
        public String location;
    }

    public static class UserOnboarding {
        @JsonProperty("userId")
        public String userId;
        @JsonProperty("step")
        public String step;
        @JsonProperty("timestamp")
        public String timestamp;
        @JsonProperty("completionStatus")
        public String completionStatus;
        @JsonProperty("deviceInfo")
        public String deviceInfo;
        @JsonProperty("ipAddress")
        public String ipAddress;
        @JsonProperty("referralSource")
        public String referralSource;
    }

    public static class AffluenceScoreUpdate {
        @JsonProperty("userId")
        public String userId;
        @JsonProperty("previousScore")
        public Integer previousScore;
        @JsonProperty("newScore")
        public Integer newScore;
        @JsonProperty("scoreChange")
        public Integer scoreChange;
        @JsonProperty("timestamp")
        public String timestamp;
        @JsonProperty("factors")
        public List<String> factors;
        @JsonProperty("confidenceLevel")
        public String confidenceLevel;
    }

    public static class AtRiskUser {
        @JsonProperty("userId")
        public String userId;
        @JsonProperty("riskScore")
        public Integer riskScore;
        @JsonProperty("riskLevel")
        public String riskLevel;
        @JsonProperty("reasons")
        public List<String> reasons;
        @JsonProperty("timestamp")
        public String timestamp;
        @JsonProperty("recommendedActions")
        public List<String> recommendedActions;
        @JsonProperty("alertId")
        public String alertId;
    }

    public static class BrandAffinityScore {
        @JsonProperty("userId")
        public String userId;
        @JsonProperty("brandName")
        public String brandName;
        @JsonProperty("affinityScore")
        public Double affinityScore;
        @JsonProperty("previousScore")
        public Double previousScore;
        @JsonProperty("scoreChange")
        public Double scoreChange;
        @JsonProperty("timestamp")
        public String timestamp;
        @JsonProperty("interactionCount")
        public Integer interactionCount;
        @JsonProperty("lastInteraction")
        public String lastInteraction;
        @JsonProperty("category")
        public String category;
    }

    // Sample data generators
    private Transaction generateTransaction() {
        Transaction transaction = new Transaction();
        transaction.userId = USER_IDS[random.nextInt(USER_IDS.length)];
        transaction.transactionId = "txn_" + UUID.randomUUID().toString().substring(0, 8);
        transaction.amount = BigDecimal.valueOf(random.nextDouble() * 1000).setScale(2, BigDecimal.ROUND_HALF_UP);
        transaction.currency = "USD";
        transaction.transactionType = TRANSACTION_TYPES[random.nextInt(TRANSACTION_TYPES.length)];
        transaction.merchant = MERCHANTS[random.nextInt(MERCHANTS.length)];
        transaction.category = CATEGORIES[random.nextInt(CATEGORIES.length)];
        transaction.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        transaction.description = "Transaction at " + transaction.merchant;
        transaction.location = "City_" + random.nextInt(50);
        return transaction;
    }

    private UserOnboarding generateUserOnboarding() {
        UserOnboarding onboarding = new UserOnboarding();
        onboarding.userId = USER_IDS[random.nextInt(USER_IDS.length)];
        onboarding.step = ONBOARDING_STEPS[random.nextInt(ONBOARDING_STEPS.length)];
        onboarding.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        onboarding.completionStatus = random.nextBoolean() ? "COMPLETED" : "IN_PROGRESS";
        onboarding.deviceInfo = "Mobile_" + (random.nextBoolean() ? "iOS" : "Android");
        onboarding.ipAddress = "192.168." + random.nextInt(255) + "." + random.nextInt(255);
        onboarding.referralSource = random.nextBoolean() ? "ORGANIC" : "REFERRAL";
        return onboarding;
    }

    private AffluenceScoreUpdate generateAffluenceScoreUpdate() {
        AffluenceScoreUpdate update = new AffluenceScoreUpdate();
        update.userId = USER_IDS[random.nextInt(USER_IDS.length)];
        update.previousScore = random.nextInt(1000);
        update.newScore = update.previousScore + random.nextInt(200) - 100;
        update.scoreChange = update.newScore - update.previousScore;
        update.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        update.factors = Arrays.asList("INCOME_INCREASE", "SPENDING_PATTERN", "INVESTMENT_ACTIVITY");
        update.confidenceLevel = random.nextBoolean() ? "HIGH" : "MEDIUM";
        return update;
    }

    private AtRiskUser generateAtRiskUser() {
        AtRiskUser atRisk = new AtRiskUser();
        atRisk.userId = USER_IDS[random.nextInt(USER_IDS.length)];
        atRisk.riskScore = random.nextInt(100);
        atRisk.riskLevel = atRisk.riskScore > 70 ? "HIGH" : atRisk.riskScore > 40 ? "MEDIUM" : "LOW";
        atRisk.reasons = Arrays.asList(
                RISK_REASONS[random.nextInt(RISK_REASONS.length)],
                RISK_REASONS[random.nextInt(RISK_REASONS.length)]
        );
        atRisk.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        atRisk.recommendedActions = Arrays.asList("VERIFY_IDENTITY", "MONITOR_TRANSACTIONS", "LIMIT_ACCOUNT");
        atRisk.alertId = "alert_" + UUID.randomUUID().toString().substring(0, 8);
        return atRisk;
    }

    private BrandAffinityScore generateBrandAffinityScore() {
        BrandAffinityScore affinity = new BrandAffinityScore();
        affinity.userId = USER_IDS[random.nextInt(USER_IDS.length)];
        affinity.brandName = BRANDS[random.nextInt(BRANDS.length)];
        affinity.previousScore = random.nextDouble() * 100;
        affinity.affinityScore = affinity.previousScore + (random.nextDouble() * 20 - 10);
        affinity.scoreChange = affinity.affinityScore - affinity.previousScore;
        affinity.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        affinity.interactionCount = random.nextInt(50);
        affinity.lastInteraction = LocalDateTime.now().minusDays(random.nextInt(30))
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        affinity.category = CATEGORIES[random.nextInt(CATEGORIES.length)];
        return affinity;
    }

    // Send methods
    private void sendEvent(String topic, String userId, Object event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, userId, eventJson);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Error sending event to topic " + topic + ": " + exception.getMessage());
                } else {
                    System.out.println("Event sent to topic: " + topic +
                            ", partition: " + metadata.partition() +
                            ", offset: " + metadata.offset() +
                            ", userId: " + userId);
                }
            });
        } catch (Exception e) {
            System.err.println("Error serializing event: " + e.getMessage());
        }
    }

    public void startStreaming() {
        // Schedule transaction events
        scheduler.scheduleAtFixedRate(() -> {
            Transaction transaction = generateTransaction();
            sendEvent(TRANSACTION_TOPIC, transaction.userId, transaction);
        }, 0, 1, TimeUnit.SECONDS);

        // Schedule onboarding events
        scheduler.scheduleAtFixedRate(() -> {
            UserOnboarding onboarding = generateUserOnboarding();
            sendEvent(ONBOARDING_TOPIC, onboarding.userId, onboarding);
        }, 0, 1, TimeUnit.SECONDS);

        // Schedule affluence score updates
        scheduler.scheduleAtFixedRate(() -> {
            AffluenceScoreUpdate update = generateAffluenceScoreUpdate();
            sendEvent(AFFLUENCE_TOPIC, update.userId, update);
        }, 0, 1, TimeUnit.SECONDS);

        // Schedule at-risk user events
        scheduler.scheduleAtFixedRate(() -> {
            AtRiskUser atRisk = generateAtRiskUser();
            sendEvent(AT_RISK_TOPIC, atRisk.userId, atRisk);
        }, 0, 1, TimeUnit.SECONDS);

        // Schedule brand affinity events
        scheduler.scheduleAtFixedRate(() -> {
            BrandAffinityScore affinity = generateBrandAffinityScore();
            sendEvent(BRAND_AFFINITY_TOPIC, affinity.userId, affinity);
        }, 0, 1, TimeUnit.SECONDS);

        System.out.println("Kafka event streaming started. Sending 1 message per second to each topic...");
    }

    public void stopStreaming() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        producer.close();
        adminClient.close();
        System.out.println("Kafka event streaming stopped.");
    }

    public static void main(String[] args) {
        KafkaEventStreamingUtility utility = new KafkaEventStreamingUtility();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(utility::stopStreaming));

        // Start streaming
        utility.startStreaming();

        // Keep the application running
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}