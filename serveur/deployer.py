import os
import sys
from distutils.dir_util import copy_tree
from shutil import copyfile

dev_folder = os.getcwd()
deploy_target_folder = "../../../../host-LOG3900//pi3" # Path to the host's git folder


def deploy():
    # copy stuff
    copy_tree(dev_folder + '\\app', deploy_target_folder + '\\app')
    copy_tree(dev_folder + '\\out', deploy_target_folder + '\\out')
    copy_tree(dev_folder + '\\test', deploy_target_folder + '\\test')
    copyfile(dev_folder + '\\package.json', deploy_target_folder + '\\package.json')
    os.chdir(deploy_target_folder)
    # push
    add_ok = not os.system('git add .')
    if add_ok:
        commit_ok = not os.system('git commit -m "deployment commit (see poly gitlab for the original commit msg)"')
        if commit_ok:
            push_ok = not os.system('git push')
            if push_ok:
                print('\n\nServer code is deployed ! Tests + release should take -1 min')

if __name__ == '__main__':
    deploy()
