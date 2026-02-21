#!/bin/bash
# Car Reservation System - Docker Quick Start Script
# This script provides easy commands for common Docker operations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Main menu
show_menu() {
    echo ""
    echo -e "${BLUE}════════════════════════════════════════${NC}"
    echo -e "${BLUE}  Car Reservation System - Docker CLI${NC}"
    echo -e "${BLUE}════════════════════════════════════════${NC}"
    echo ""
    echo "1) Start application (docker compose up -d)"
    echo "2) Stop application (docker compose down)"
    echo "3) Restart application"
    echo "4) View application logs (follow)"
    echo "5) View MySQL logs (follow)"
    echo "6) Run tests"
    echo "7) Check container status"
    echo "8) Open database shell"
    echo "9) Open app shell"
    echo "10) Clean up (remove containers & volumes)"
    echo "11) Build images"
    echo "12) View API health status"
    echo "13) Create database backup"
    echo "0) Exit"
    echo ""
}

# Function to start application
start_app() {
    print_info "Starting application..."
    docker compose up -d
    print_info "Waiting for services to be ready..."
    sleep 10
    
    # Check health
    if docker compose exec app curl -f http://localhost:8080/actuator/health &> /dev/null; then
        print_success "Application is running!"
        echo ""
        echo "Access points:"
        echo "  API: http://localhost:8080"
        echo "  API Health: http://localhost:8080/actuator/health"
        echo "  Database: localhost:3306 (theuser/password)"
    else
        print_warning "Application is starting, but not yet ready. Check logs with option 4"
    fi
}

# Function to stop application
stop_app() {
    print_info "Stopping application..."
    docker compose down
    print_success "Application stopped"
}

# Function to restart application
restart_app() {
    print_info "Restarting application..."
    docker compose restart
    print_success "Application restarted"
}

# Function to view app logs
view_app_logs() {
    print_info "Displaying application logs (press Ctrl+C to stop)..."
    docker compose logs -f app
}

# Function to view MySQL logs
view_mysql_logs() {
    print_info "Displaying MySQL logs (press Ctrl+C to stop)..."
    docker compose logs -f mysql
}

# Function to run tests
run_tests() {
    print_info "Running tests..."
    docker compose run --rm app ./gradlew test
    print_success "Tests completed!"
}

# Function to check status
check_status() {
    print_info "Container Status:"
    docker compose ps
    echo ""
    print_info "Network Status:"
    docker network ls | grep car-reservation
}

# Function to open database shell
open_db_shell() {
    print_info "Opening MySQL shell..."
    docker compose exec mysql mysql -u theuser -pthepassword car_rsvt
}

# Function to open app shell
open_app_shell() {
    print_info "Opening application shell..."
    docker compose exec app sh
}

# Function to cleanup
cleanup() {
    print_warning "This will remove all containers and volumes. Continue? (y/N)"
    read -r response
    if [[ "$response" == "y" || "$response" == "Y" ]]; then
        print_info "Cleaning up..."
        docker compose down -v
        print_success "Cleanup completed"
    else
        print_info "Cleanup cancelled"
    fi
}

# Function to build
build_images() {
    print_info "Building Docker images..."
    docker compose build
    print_success "Build completed!"
}

# Function to check health
check_health() {
    print_info "Checking API health..."
    if curl -s http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
        print_success "API is healthy ✓"
    else
        print_error "API is not responding"
    fi
}

# Function to backup database
backup_database() {
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_FILE="mysql_backup_${TIMESTAMP}.tar.gz"
    print_info "Creating database backup..."
    docker run --rm -v mysql_data:/data -v "$(pwd)":/backup \
        busybox tar czf "/backup/${BACKUP_FILE}" -C /data .
    print_success "Database backed up to ${BACKUP_FILE}"
}

# Main loop
while true; do
    show_menu
    read -p "Select option (0-13): " choice
    
    case $choice in
        1) start_app ;;
        2) stop_app ;;
        3) restart_app ;;
        4) view_app_logs ;;
        5) view_mysql_logs ;;
        6) run_tests ;;
        7) check_status ;;
        8) open_db_shell ;;
        9) open_app_shell ;;
        10) cleanup ;;
        11) build_images ;;
        12) check_health ;;
        13) backup_database ;;
        0) print_info "Goodbye!"; exit 0 ;;
        *) print_error "Invalid option. Please try again." ;;
    esac
done
