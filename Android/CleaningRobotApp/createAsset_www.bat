@echo off
IF NOT exist assets/www/ ( 
    cd assets
    mklink /d www "..\webresources\dist\android\www"
)

