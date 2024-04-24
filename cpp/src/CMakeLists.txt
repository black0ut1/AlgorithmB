set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -pthread")

set(BP_Cpp_SOURCES
        ${CMAKE_SOURCE_DIR}/src/data/network/Network.cpp
        ${CMAKE_SOURCE_DIR}/src/data/network/Network.h
        ${CMAKE_SOURCE_DIR}/src/data/matrix/Matrix.cpp
        ${CMAKE_SOURCE_DIR}/src/data/matrix/Matrix.h
        ${CMAKE_SOURCE_DIR}/src/data/bush/Bush.cpp
        ${CMAKE_SOURCE_DIR}/src/data/bush/Bush.h
        ${CMAKE_SOURCE_DIR}/src/io/parser/Parser.cpp
        ${CMAKE_SOURCE_DIR}/src/io/parser/Parser.h
        ${CMAKE_SOURCE_DIR}/src/data/priority_queue/PriorityQueue.cpp
        ${CMAKE_SOURCE_DIR}/src/data/priority_queue/PriorityQueue.h
        ${CMAKE_SOURCE_DIR}/src/algorithm/algorithm/Algorithm.cpp
        ${CMAKE_SOURCE_DIR}/src/algorithm/algorithm/Algorithm.h
        ${CMAKE_SOURCE_DIR}/src/algorithm/parallel_algorithm/ParallelAlgorithm.cpp
        ${CMAKE_SOURCE_DIR}/src/algorithm/parallel_algorithm/ParallelAlgorithm.h
        ${CMAKE_SOURCE_DIR}/src/io/writer/Writer.cpp
        ${CMAKE_SOURCE_DIR}/src/io/writer/Writer.h
        ${CMAKE_SOURCE_DIR}/src/data/queue/Queue.cpp
        ${CMAKE_SOURCE_DIR}/src/data/queue/Queue.h
        CACHE INTERNAL BP_Cpp_SOURCES)

add_executable(BP_Cpp main.cpp ${BP_Cpp_SOURCES})
