import pygame

init_status = pygame.init()
print(f"Pygame initialized: {init_status}")

import random

pygame.init()

WIDTH, HEIGHT = 800, 600
BALL_SIZE = 20
PADDLE_WIDTH, PADDLE_HEIGHT = 10, 100

WHITE = (255, 255, 255)
BLACK = (0, 0, 0)

screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Pong Game")

clock = pygame.time.Clock()

ball_x = WIDTH // 2
ball_y = HEIGHT // 2
ball_dx = random.choice([-5, 5])
ball_dy = random.choice([-5, 5])

paddle_left_y = HEIGHT // 2 - PADDLE_HEIGHT // 2
paddle_right_y = HEIGHT // 2 - PADDLE_HEIGHT // 2
paddle_speed = 7

score_left = 0
score_right = 0

font = pygame.font.Font(None, 74)

running = True
while running:
    screen.fill(BLACK)

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    keys = pygame.key.get_pressed()
    if keys[pygame.K_w] and paddle_left_y > 0:
        paddle_left_y -= paddle_speed
    if keys[pygame.K_s] and paddle_left_y < HEIGHT - PADDLE_HEIGHT:
        paddle_left_y += paddle_speed
    if keys[pygame.K_UP] and paddle_right_y > 0:
        paddle_right_y -= paddle_speed
    if keys[pygame.K_DOWN] and paddle_right_y < HEIGHT - PADDLE_HEIGHT:
        paddle_right_y += paddle_speed

    ball_x += ball_dx
    ball_y += ball_dy

    if ball_y <= 0 or ball_y >= HEIGHT - BALL_SIZE:
        ball_dy *= -1

    if (ball_x <= PADDLE_WIDTH and paddle_left_y <= ball_y <= paddle_left_y + PADDLE_HEIGHT) or \
       (ball_x >= WIDTH - PADDLE_WIDTH - BALL_SIZE and paddle_right_y <= ball_y <= paddle_right_y + PADDLE_HEIGHT):
        ball_dx *= -1

    if ball_x < 0:
        score_right += 1
        ball_x, ball_y = WIDTH // 2, HEIGHT // 2
        ball_dx *= -1
        ball_dy = random.choice([-5, 5])
    elif ball_x > WIDTH:
        score_left += 1
        ball_x, ball_y = WIDTH // 2, HEIGHT // 2
        ball_dx *= -1
        ball_dy = random.choice([-5, 5])

    pygame.draw.rect(screen, WHITE, (0, paddle_left_y, PADDLE_WIDTH, PADDLE_HEIGHT))
    pygame.draw.rect(screen, WHITE, (WIDTH - PADDLE_WIDTH, paddle_right_y, PADDLE_WIDTH, PADDLE_HEIGHT))
    pygame.draw.ellipse(screen, WHITE, (ball_x, ball_y, BALL_SIZE, BALL_SIZE))
    score_text = font.render(f"{score_left}   {score_right}", True, WHITE)
    screen.blit(score_text, (WIDTH // 2 - score_text.get_width() // 2, 10))

    pygame.display.flip()

    clock.tick(60)

pygame.quit()
