# CTSSBench

The CTSSBench is an open source benchmark for automatic configuration tuning research on software systems. The CTSSBench mainly helps users to conveniently evaluate and compare the state-of-the-art configuration tuning algorithms in academia and industry, and further choose an appropriate algorithm for a specific software system.

Our website is [http://120.27.69.55:8020](http://120.27.69.55:8020), where you can find more information about CTSSBench, including some related papers on smart tuning algorithms, systems under tune (such as hadoop, redis, tomcat and other software systems), tuning results, as well as our research papers and source code on Colosseum. If you are interested, you can experience the CTSSBench project in the [demo](http://120.27.69.55:8020/#/login)

## Note

The CTSSBench currently provides three parameter selection approaches (**select_all**, **lasso_select** and **sensitivity_analysis**) , one sampling method (**random_sample**), two performance modeling methods (**gaussian_process** and **random_forest**), and six search strategies (**actgan**, **genetic_algorithm**, **random_search**, **space_search**, **random_tuning**, and **bo**). 

## Preparation

Prepare a cloud server, and successfully install the system under tune (SUT) supported by the Colosseum and the corresponding workload on the server.

Register or use an existing account on [the login page](http://120.27.69.55:8020/#/login) to log in to Colosseum Demo.

## Usage

First, you need to create an experimental environment in the environment module. During this process, you need to fill in three parts of information:

1. The connection information of the cloud server, including the server's IP address, login username, password, etc.

2. The configuration information of the SUT, including the name, version number, installation path, test commands, etc.

3. The configuration information of the workload, including the name, version number, installation path, test command, etc.

After that, we need to create a tuning algorithm. The algorithm module provides multiple algorithm components. You can drag and connect the algorithm components of different functions on the front page, and click each component to assign values to the parameters of each component, and finally save to generate a running algorithm. Moreover, you can also upload your own algorithm files implemented in python programming language, and use them through the tuning experiments.

Now you can use the Colosseum for configuration tuning experiments. You need to create an experiment that contains an experimental environment and one or more tuning algorithms. After creating the experiment, you can run it one or more times, and view the results of the tuning experiment in the running history.
