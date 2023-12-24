#!/bin/bash

# Set LocalStack configuration
ACCESS_KEY="foo"
SECRET_KEY="bar"
DEFAULT_REGION="us-east-1"
LOCALSTACK_ENDPOINT="http://localhost:4566"
AWS_REGION="us-east-1"


docker rm -f myLocalStack

# Start LocalStack Docker container
docker run --name myLocalStack -d --rm -p 4566-4599:4566-4599 -p 8055:8080 -e SERVICES=s3,dynamodb -e DEFAULT_REGION=$DEFAULT_REGION -e AWS_ACCESS_KEY_ID=$ACCESS_KEY -e AWS_SECRET_ACCESS_KEY=$SECRET_KEY -e EDGE_PORT=8055 localstack/localstack

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to be ready..."
while ! nc -z localhost 4566; do
  sleep 1
done

# Set AWS CLI configuration for LocalStack
aws configure --profile localstack << EOF
$ACCESS_KEY
$SECRET_KEY
$DEFAULT_REGION
json
EOF

echo 

echo "Creating tables."

# Folder containing JSON files
json_folder="./tables"

# Iterate through JSON files in the folder
for json_file in "$json_folder"/*.json; do
    if [ -f "$json_file" ]; then
        table_name=$(basename "$json_file" .json)

        # Create DynamoDB table using AWS CLI
        aws dynamodb create-table \
            --cli-input-json "file://${json_file}" \
            --endpoint-url "$LOCALSTACK_ENDPOINT" \
            --region "$AWS_REGION" \
            --profile localstack \
            > /dev/null 2>&1 &

        if [ $? -eq 0 ]; then
            echo "Table '$table_name' created successfully."
        else
            echo "Error creating table '$table_name'."
        fi
    fi
done

echo "Table creation process complete."

echo "LocalStack setup complete."
echo "You can now use the AWS CLI with LocalStack using the 'localstack' profile."
