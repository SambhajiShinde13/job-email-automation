#!/bin/bash

# API Testing Script for Job Email Automation System

API_URL="http://localhost:8080/api/contacts"

echo "========================================="
echo "Job Email Automation - API Tests"
echo "========================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Test 1: Health Check
echo "Test 1: Health Check"
response=$(curl -s -w "\n%{http_code}" "$API_URL/health")
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
    echo "$body" | jq .
else
    echo -e "${RED}✗ FAILED${NC}"
    echo "HTTP Code: $http_code"
fi
echo ""

# Test 2: Get Statistics
echo "Test 2: Get Statistics"
response=$(curl -s -w "\n%{http_code}" "$API_URL/stats")
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
    echo "$body" | jq .
else
    echo -e "${RED}✗ FAILED${NC}"
    echo "HTTP Code: $http_code"
fi
echo ""

# Test 3: Add New Contact
echo "Test 3: Add New Contact"
response=$(curl -s -w "\n%{http_code}" -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "hrName": "Test HR",
    "email": "test_'$(date +%s)'@example.com",
    "company": "Test Company",
    "position": "Test Position",
    "active": true
  }')
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 201 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
    echo "$body" | jq .
    contact_id=$(echo "$body" | jq -r .id)
else
    echo -e "${RED}✗ FAILED${NC}"
    echo "HTTP Code: $http_code"
fi
echo ""

# Test 4: Get All Contacts
echo "Test 4: Get All Contacts"
response=$(curl -s -w "\n%{http_code}" "$API_URL")
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
    count=$(echo "$body" | jq 'length')
    echo "Total contacts: $count"
else
    echo -e "${RED}✗ FAILED${NC}"
    echo "HTTP Code: $http_code"
fi
echo ""

# Test 5: Get Active Contacts
echo "Test 5: Get Active Contacts"
response=$(curl -s -w "\n%{http_code}" "$API_URL/active")
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
    count=$(echo "$body" | jq 'length')
    echo "Active contacts: $count"
else
    echo -e "${RED}✗ FAILED${NC}"
    echo "HTTP Code: $http_code"
fi
echo ""

# Test 6: Get Contact by ID (if we have one from Test 3)
if [ ! -z "$contact_id" ]; then
    echo "Test 6: Get Contact by ID ($contact_id)"
    response=$(curl -s -w "\n%{http_code}" "$API_URL/$contact_id")
    http_code=$(echo "$response" | tail -n 1)
    body=$(echo "$response" | sed '$d')

    if [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✓ PASSED${NC}"
        echo "$body" | jq .
    else
        echo -e "${RED}✗ FAILED${NC}"
        echo "HTTP Code: $http_code"
    fi
    echo ""
fi

# Test 7: Bulk Add Contacts
echo "Test 7: Bulk Add Contacts"
response=$(curl -s -w "\n%{http_code}" -X POST "$API_URL/bulk" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "hrName": "Bulk Test 1",
      "email": "bulk1_'$(date +%s)'@example.com",
      "company": "Bulk Company 1",
      "position": "Position 1",
      "active": true
    },
    {
      "hrName": "Bulk Test 2",
      "email": "bulk2_'$(date +%s)'@example.com",
      "company": "Bulk Company 2",
      "position": "Position 2",
      "active": true
    }
  ]')
http_code=$(echo "$response" | tail -n 1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" -eq 201 ]; then
    echo -e "${GREEN}✓ PASSED${NC}"
    echo "$body" | jq .
else
    echo -e "${RED}✗ FAILED${NC}"
    echo "HTTP Code: $http_code"
fi
echo ""

echo "========================================="
echo "API Tests Completed"
echo "========================================="
