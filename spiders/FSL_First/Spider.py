__author__ = 'damons'

import subprocess
import os,sys
from dax import XnatUtils,SpiderProcessHandler
from datetime import datetime
import shutil

def parse_args():
    from argparse import ArgumentParser
    des = 'Spider to run FSL First sub-cortical script.'
    ap = ArgumentParser(description=des)
    ap.add_argument('-p', '--project', dest='project', help='Project Label', required=False)
    ap.add_argument('-s', '--subject', dest='subject', help='Subject Label', required=False)
    ap.add_argument('-e', '--experiment', dest='session', help='Session Label', required=False)
    ap.add_argument("-c", "--scan", dest="scan", help="scan ID on Xnat or full path to file for offline", required=True)
    ap.add_argument("-d", dest="directory", help="scan ID on Xnat or full path to file for offline", required=True)
    ap.add_argument("--offline", dest='offline',action='store_true', help="run spider without XNAT", required=False)
    ap.add_argument('--suffix', dest='suffix', help='assessor suffix. default: None', default=None)
    return ap.parse_args()

def init(options):
        #init arguments:
    arguments={}
    #XNAT:
    arguments['project']=options.project
    arguments['subject']=options.subject
    arguments['session']=options.session
    arguments['scan']=options.scan
    arguments['jobdir'] = options.directory
    arguments['suffix'] = options.suffix

    if not os.path.isdir(arguments['jobdir']):
        os.makedirs(arguments['jobdir'])
    else:
        shutil.rmtree(arguments['jobdir'])
        os.makedirs(arguments['jobdir'])
    if not os.path.isdir(os.path.join(arguments['jobdir'],'Inputs')):
        os.makedirs(os.path.join(arguments['jobdir'],'Inputs'))
    else:
        shutil.rmtree(os.path.join(arguments['jobdir'],'Inputs'))
        os.makedirs(os.path.join(arguments['jobdir'],'Inputs'))
    if not os.path.isdir(os.path.join(arguments['jobdir'],'Outputs')):
        os.makedirs(os.path.join(arguments['jobdir'],'Outputs'))
    else:
        shutil.rmtree(os.path.join(arguments['jobdir'],'Outputs'))
        os.makedirs(os.path.join(arguments['jobdir'],'Outputs'))
    Inputdir = os.path.join(arguments['jobdir'],'Inputs')
    if options.offline:
        if not os.path.isfile(options.scan):
            sys.stderr.write("ERROR: %s does not exist" % options.scan)
        shutil.copyfile(arguments['filepath'], os.path.join(arguments['jobdir'],'Inputs', 'T1.nii.gz'))
        arguments['filepath'] = os.path.join(arguments['jobdir'],'Inputs', 'T1.nii.gz')
    else:
        XnatUtils.download_Scan(Inputdir,options.project,options.subject,options.session,options.scan,['NIFTI'])

        if not os.listdir(Inputdir):
            print 'ERROR: No Inputs downloaded.\n'
            sys.exit()
        niifile  = os.listdir(Inputdir)[0]
        os.rename(os.path.join(Inputdir,niifile),os.path.join(Inputdir,'T1.nii.gz'))

    return arguments

def run_FSL_First(jobdir,**kwargs):
    #print arguments:
    print 'docker run --rm -ti --mac-address 02:42:ac:11:00:02 -v %s/Inputs:/home/Inputs -v %s/Outputs:/home/Output/ spiders/fsl_first:latest xvfb-run -a /opt/MATLAB/R2016a/bin/matlab \< /home/run.m ' %(jobdir,jobdir)
    os.system('docker run --rm -ti --mac-address 02:42:ac:11:00:02 -v %s/Inputs:/home/Inputs -v %s/Outputs:/home/Output/ spiders/fsl_first:latest xvfb-run -a /opt/MATLAB/R2016a/bin/matlab \< /home/run.m' %(jobdir,jobdir))

    print '===================================================================\n'

def finish_FSL_First(suffix,project,subject,session,scan,jobdir,**kwargs):
    EndOfSpider=SpiderProcessHandler('FSL_First',suffix,project,subject,session,scan)
    ### MATLAB Folder ###
    EndOfSpider.add_folder(os.path.join(jobdir,'MATLAB'))
    ### PDF ###
    EndOfSpider.add_pdf(os.path.join(jobdir,'Outputs','RESULT.pdf'))
    ### NII ###
    EndOfSpider.add_file(os.path.join(jobdir,'Outputs','RESULT.nii.gz'),'NII')
    ### STATS ###
    EndOfSpider.add_file(os.path.join(jobdir,'Outputs','RESULT.CSV'),'STATS')
    ### Saving the output folder ###
    EndOfSpider.add_folder(os.path.join(jobdir,'Outputs'),resource_name='All_results')
    ### Job Done and Clean the directory ###
    EndOfSpider.done()
    EndOfSpider.clean(jobdir)

if __name__ == '__main__':
    ################### Script for dtiQA on one session given by the options into the project in Xnat ######################
    args = parse_args()
    print '-------- Spider starts --------'
    print 'Time at the beginning of the Spider: ', str(datetime.now())
    print 'INFO: Arguments'
    XnatUtils.print_args(args)
    print 'INFO: Initialisation'
    arguments=init(args)



    print 'INFO: Running FSL_First version 1.0.0'
    run_FSL_First(**arguments)

    print 'INFO: End of Spider'
    finish_FSL_First(**arguments)

    #print time at the end of the spider
    print '\nTime at the end of the Spider: ', str(datetime.now())

