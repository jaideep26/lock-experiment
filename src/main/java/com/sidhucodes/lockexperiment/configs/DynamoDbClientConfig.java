package com.sidhucodes.lockexperiment.configs;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbClientConfig {

    @Value("${cloud.aws.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.endpoint.uri}")
    private String endpointUri;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${cloud.aws.secretKey}")
    private String secretKey;

    @Bean
    DynamoDbClient dynamoDbClient() throws Exception{
        return DynamoDbClient.builder()
                .region(Region.of(awsRegion)) //
                .endpointOverride(new URI(endpointUri))//
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

}
