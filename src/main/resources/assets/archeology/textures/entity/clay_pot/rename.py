"""
This script is used to rename textures exported Aseprite
as numbered frames to correct names colors appended
"""

import re
import os

names = [
  "clay_pot",
  "clay_pot_black",
  "clay_pot_red",
  "clay_pot_green",
  "clay_pot_brown",
  "clay_pot_blue",
  "clay_pot_purple",
  "clay_pot_cyan",
  "clay_pot_light_gray",
  "clay_pot_gray",
  "clay_pot_pink",
  "clay_pot_lime",
  "clay_pot_yellow",
  "clay_pot_light_blue",
  "clay_pot_magenta",
  "clay_pot_orange",
  "clay_pot_white"
]

path = os.path.dirname(__file__)
files = os.listdir(path)

def condition(name):
    return re.match(r'^clay_pot\d+.png$', name)

images = list(filter(condition, files))
images.sort(key = lambda x: int(x[8:-4]))

for name, new_name in zip(images, names):
    new_name += ".png"
    os.replace(name, new_name)
    print("Renamed", name, "into", new_name)
