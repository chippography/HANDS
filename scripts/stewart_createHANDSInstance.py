import shutil, errno, os, subprocess, datetime, time, sys

nocopy = False
nokill = False
nodelete = False
schedule_file_name = "simulationSchedule.txt"

if ( len(sys.argv) > 0 ):
    for arg in sys.argv:
        if ( arg == "-ncp"):
            nocopy = True
            nodelete = True
        if ( arg == "-nk"):
            nokill = True
        if ( arg == "-ss"):
            schedule_file_name = sys.argv[sys.argv.index("-ss") + 1]
        
ts = time.time()

dropbox_directory = "<Directory>"
mounted_drive_directory = "<Directory>"

print "Create new HANDS instance"
var = raw_input("Use (1) Local dropbox (2) Mounted drive for copy ");

print var;

if ( var == "1" ):
    root_copy_directory = dropbox_directory;
else: 
    root_copy_directory = mounted_drive_directory
    
jar_directory = root_copy_directory + "/bin"
lib_directory = root_copy_directory + "/lib"
schedule_file = root_copy_directory + "/output/" + schedule_file_name

target_directory = "<Desktop>" + datetime.datetime.fromtimestamp(ts).strftime('%Y%m%d_%H%M%S')

print "Creating files at " + target_directory + "..."

if not os.path.exists(target_directory):
    os.makedirs(target_directory)
    os.makedirs(target_directory + "/output")
    os.makedirs(target_directory + "/output/data")
    os.makedirs(target_directory + "/output/dataArchive")
    os.makedirs(target_directory + "/output/data/charts")
    file = open(target_directory + "/output/simRecordID.txt",'w')

def rename(src, dst):
    os.rename(src, dst)
    
def copyanything(src, dst):
    try:
        shutil.copytree(src, dst)
    except OSError as exc: # python >2.5
        if exc.errno == errno.ENOTDIR:
            shutil.copy(src, dst)
        else: raise

def copymultiple(src, dst):
    src_files = os.listdir(src)
    for file_name in src_files:
        full_file_name = os.path.join(src, file_name)
        if (os.path.isfile(full_file_name)):
            shutil.copy(full_file_name, dst)
  
print "Copying bin from " + jar_directory + "..."
      
copyanything(jar_directory, target_directory + "/bin")

print "Copying lib from " + lib_directory + "... (takes ages)"

copyanything(lib_directory, target_directory + "/lib")

print "Copying schedule from " + schedule_file + "..."

copyanything(schedule_file, target_directory + "/output")

rename(target_directory + "/output/" + schedule_file_name, target_directory + "/output/simulationSchedule.txt")

os.chdir(target_directory)

my_env = os.environ.copy();
my_env["AWT_FORCE_HEADFUL"] = "true";

print "Running HANDS..."

try:
    if (nokill == True):
        subprocess.call(['/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java', '-XX:-UseGCOverheadLimit', '-Xmx6g', '-Dfile.encoding=US-ASCII', '-classpath', target_directory + '/bin:<WorkspacePath>lib/epsgraphics.jar:<WorkspacePath>lib/jcommon-1.0.21.jar:<WorkspacePath>lib/jfreechart-1.0.17.jar:<WorkspacePath>lib/jgrapht-core-0.9.0.jar:<WorkspacePath>lib/jgraph-sna.jar:<WorkspacePath>lib/java-plot.jar:<WorkspacePath>lib/commons-math3-3.4.1.jar:<WorkspacePath>lib/bsh-2.0b4.jar:<WorkspacePath>lib/mapdb.jar:<WorkspacePath>lib/fst-2.38-onejar.jar', 'Utility.Runner', '-t'], env=my_env)
    else:
        subprocess.call(['/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java', '-XX:-UseGCOverheadLimit', '-Xmx6g', '-Dfile.encoding=US-ASCII', '-classpath', target_directory + '/bin:<WorkspacePath>lib/epsgraphics.jar:<WorkspacePath>lib/jcommon-1.0.21.jar:<WorkspacePath>lib/jfreechart-1.0.17.jar:<WorkspacePath>lib/jgrapht-core-0.9.0.jar:<WorkspacePath>lib/jgraph-sna.jar:<WorkspacePath>lib/java-plot.jar:<WorkspacePath>lib/commons-math3-3.4.1.jar:<WorkspacePath>lib/bsh-2.0b4.jar:<WorkspacePath>lib/mapdb.jar:<WorkspacePath>lib/fst-2.38-onejar.jar', 'Utility.Runner', '-t', '-k'], env=my_env)
except KeyboardInterrupt:
    print "Quitting HANDS."
    nocopy = True
   
if (nocopy == False):
    print "Simulation(s) complete. Moving files to main directory."

    copymultiple(target_directory + "/output/data/", root_copy_directory + "/output/data/")

if (nodelete == False):
    print "Deleting temporary directory " + target_directory

    shutil.rmtree(target_directory)
