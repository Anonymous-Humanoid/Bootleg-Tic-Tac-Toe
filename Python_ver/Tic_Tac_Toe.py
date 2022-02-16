#!/usr/bin/env python
# Matthew Simpson
# Bootleg Tic-Tac-Toe
# Classic Tic-Tac-Toe

import pygame

global grid, values, x_off, y_off

ITERATION = range(3)

def __set_cell(row, col, is_x_turn):
    global grid, values, x_off, y_off

    img = 'X' if is_x_turn else 'O'

    # Updating cell
    grid[row][col] = (
        pygame.image.load(img + '.jpg').convert_alpha(),
        pygame.image.load(img + '.jpg').convert_alpha().get_rect()
    )

    # Aligning cell
    grid[row][col][1].x = x_off + col * grid[row][col][1].width
    grid[row][col][1].y = y_off + row * grid[row][col][1].height

    # Updating cell value
    values[row][col] = is_x_turn

def __match_exists():

    # Checking rows and columns for match
    for i in ITERATION:
        row = col = True

        if values[i][i] != None:
            for j in ITERATION:
                col = col and values[i][i] == values[i][j]
                row = row and values[i][i] == values[j][i]
            
            if row or col:
                return True
    
    ldiag = rdiag = values[1][1] != None
    
    # Checking diagonals for match iff center cell isn't null
    if ldiag:
        for i in ITERATION:
            if values[0][0] != values[i][i]:
                ldiag = False
            if values[0][-1] != values[i][~i]:
                rdiag = False
            
    return ldiag or rdiag

def main(display=None, x=25, y=25):
    '''Initializes the game of Tic-Tac-Toe at corner (*x*, *y*) in frame *display*, if any, otherwise those arguments are automatically generated'''

    global grid, values, x_off, y_off
    
    # Setting up display if none (200 x 200px)
    if display == None:
        display = pygame.display.set_mode((200, 200))
        pygame.display.set_caption('Tic-Tac-Toe')

    # Tracking in-game time
    clock = pygame.time.Clock()

    # Initializing game variables # TODO Add option to choose who goes first?
    is_x_turn = True

    # Initializing grid variables
    x_off, y_off = x, y
    values = [[None for _ in ITERATION] for _ in ITERATION]
    grid = [
        [
            (
                pygame.image.load('Empty.jpg').convert_alpha(),
                pygame.image.load('Empty.jpg').convert_alpha().get_rect()
            )
            for _ in ITERATION
        ]
        for _ in ITERATION
    ]

    # Aligning and drawing grid cells
    for row in ITERATION:
        for col in ITERATION:
            grid[row][col][1].x = x_off + col * grid[row][col][1].width
            grid[row][col][1].y = y_off + row * grid[row][col][1].height
            
            display.blit(grid[row][col][0], grid[row][col][1])
    
    # Refreshing frame
    pygame.display.update()

    clicked = 0

    # Running game until frame is closed
    while True:

        # Handling program termination
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                raise SystemExit
            
            nextTurn = False

            # TODO DEBUG Detect and parse cell clicks
            for i in ITERATION:
                for j in ITERATION:
                    if pygame.mouse.get_pressed()[0] and grid[i][j][1].collidepoint(pygame.mouse.get_pos()) and values[i][j] == None:

                        # Updating clicked cell
                        __set_cell(i, j, is_x_turn)

                        display.blit(grid[i][j][0], grid[i][j][1])

                        # Refreshing frame
                        pygame.display.update()
                        
                        # Marking another cell clicked
                        clicked += 1
        
                        # TODO Parsing game over
                        if __match_exists():
                            print(('X' if is_x_turn else 'O') + ' wins')

                            input("Hit enter in the terminal to quit")

                            raise SystemExit
                        
                        elif clicked == 9:
                            print('Tie game')

                            input("Hit enter in the terminal to quit")

                            raise SystemExit
                        
                        # Updating player's turn
                        is_x_turn = not is_x_turn
                        
                        # Cell clicked: breaking out of loops
                        break
        
        # Refreshing display at 30fps
        clock.tick(30)
    
    # TODO Restart/quit game option?

# Main driver code
if __name__ == '__main__':

    # Starting up Pygame
    pygame.init()

    # Starting up Tic-Tac-Toe game
    main()
