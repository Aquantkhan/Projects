kaboom({
    global: true,
    fullscreen: true,
    scale: 2,
    debug: true,
    clearColor: [0, 1, 1, 1],
})



loadRoot('https://i.imgur.com/');
loadSprite('mario', 'vlJ35VF.png');
loadSprite('coin', 'wbKxhcd.png');
loadSprite('evil-shroom','KPO3fR9.png');
loadSprite('brick', 'pogC9x5.png');
loadSprite('block', 'M6rwarW.png');
loadSprite('mushroom', '0wMd92p.png');
loadSprite('surprise', 'gesQ1KP.png');
loadSprite('unboxed', 'bdrLpi6.png');
loadSprite('pipe-top-left', 'ReTPiWY.png');
loadSprite('pipe-top-right', 'hj2GK4n.png');
loadSprite('pipe-bottom-left', 'c1cYSbt.png');
loadSprite('pipe-bottom-right', 'nqQ79eI.png');

const MOVE_SPEED = 120;
const JUMP_FORCE = 400;
let current_jump_force = JUMP_FORCE;
const BIG_JUMP_FORCE = 550;
let isJumping = true;
const FALL_DEATH = 400;
const ENEMY_SPEED = 20;


scene("game", ({level, score, number_of_enemies}, ) =>
{
    layers(['bg', 'obj','ui'], 'obj')

    let current_number_of_enemies = 0;
    const map = 
    [
        '                                        ',
        '                                        ',
        '                                        ',
        '                                        ',
        '                                        ',
        '                                        ',
        '                                        ',
        '                                        ',
        '                                        ',
        '                                        ',
        '      %    =*=%=                        ',
        '                                        ',
        '                              -+        ',
        '                      ^    ^  ()        ',
        '================================   ====='
    ];


    const levelCfg = 
    {
        width: 20,
        height: 20,
        '=': [sprite('block'), solid()],
        '$': [sprite('coin'), 'coin'],
        '%': [sprite('surprise'), solid(), 'coin-surprise'],
        '*': [sprite('surprise'), solid(), 'mushroom-surprise'],
        '}': [sprite('unboxed'), solid()],
        '(': [sprite('pipe-bottom-left'), solid(), scale(0.5)],
        ')': [sprite('pipe-bottom-right'), solid(), scale(0.5)],
        '-': [sprite('pipe-top-left'), 'pipe', solid(), scale(0.5)],
        '+': [sprite('pipe-top-right'), 'pipe', solid(), scale(0.5)],
        '^': [sprite('evil-shroom'), solid(), body(), 'dangerous'],
        '#': [sprite('mushroom'), solid(), 'mushroom', body()],
    };



    const gameLevel = addLevel(map, levelCfg);


    const scorelabel = add([
        text(score),
        pos(30, 6),
        layer('ui'),
        {
            value: score,
        }
        
    ])

    add([text ('level ' + parseInt(level + 1)), pos(60,6)]);

    function big()
    {
        let timer = 0
        let isBig = false
        return {
            update() 
            {
                if(isBig)
                {
                    timer -= dt()
                    current_jump_force = BIG_JUMP_FORCE;
                    if(timer <= 0)
                    {
                        this.smallify()
                    }
                }
            },
            isBig()
            {
                return isBig;
            },
            smallify()
            {
                this.scale = vec2(0.05);
                current_jump_force = JUMP_FORCE;
                timer = 0;
                isBig = false;
            },
            biggify(time)
            {
                this.scale = vec2(0.1);
                timer = time;
                isBig = true;
            }
        }

    }

    const player = add([
        scale(0.05),
        sprite('mario'),
        solid(),
        pos(30, 0),
        body(),
        big(),
        origin('bot'),
    ]);


    action('mushroom', (m) => 
    {
        m.move(100,0);
    });

    action(() => {
        if(current_number_of_enemies < number_of_enemies)
        {
            // gameLevel.spawn('^', gridPos.sub(0,0));
            console.log(current_number_of_enemies);
            current_number_of_enemies++;
        }
    })


    player.on("headbump", (obj) => {
        if(obj.is('coin-surprise'))
        {
            gameLevel.spawn('$', obj.gridPos.sub(0, 1));
            destroy(obj);
            gameLevel.spawn('}', obj.gridPos.sub(0));
        }
        if(obj.is('mushroom-surprise'))
        {
            gameLevel.spawn('#', obj.gridPos.sub(0, 1));
            destroy(obj);
            gameLevel.spawn('}', obj.gridPos.sub(0));
        }
    })

 

    action('dangerous', (d) => 
    {
        if(d.pos.x > 50)
        {
            d.move(-ENEMY_SPEED,0);
        }
    })


    player.collides('mushroom', (m) =>
    {
        destroy(m);
        player.biggify(6);
    })

    player.collides('pipe', () =>
    {
        keyPress('down', () => 
        {
            go('game', 
            {
                level: (level + 1),
                score: scorelabel.value,
                number_of_enemies: number_of_enemies + 1,
            })
        })
    })

    player.collides('dangerous', (d) => 
    {
        if(isJumping)
        {
            destroy(d);
        }
        else
        {
        go('lose', { score: scorelabel.value})
        }
    })

    player.action(() => 
    {
        camPos(player.pos)
        if(player.pos.y >= FALL_DEATH)
        {
            go('lose', {score: scorelabel.value});
        }
    })

    player.collides('coin', (c) => 
    {
        
        destroy(c)
        scorelabel.value++;
        scorelabel.text = scorelabel.value;
    })
    keyDown('left', () => 
    {
        player.move(-MOVE_SPEED, 0)
    });

    keyDown('right', () => 
    {
        player.move(MOVE_SPEED, 0)
    });

    player.action(() => 
    {
        if(player.grounded())
        {
            isJumping = false;
        }
    })
    keyPress('space', () =>
    {
        if(player.grounded())
        {
            isJumping = true;
            player.jump(current_jump_force);
        }
    });

});

scene('lose', ({ score }) => {
    add([text(`Your score is ${score}`, 32), origin('center'), pos(width() / 2, height() / 2)])
})

start("game", {level: 0 , score: 0});