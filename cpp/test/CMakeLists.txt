set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread")

include_directories(${CMAKE_SOURCE_DIR}/src)

add_executable(BP_Cpp_tests test.cpp ${BP_Cpp_SOURCES}
        algorithm/FindFlowDeltaTest.cpp
        algorithm/GetTreesTest.cpp
        algorithm/ImproveBushTest.cpp
        algorithm/LcaTest.cpp
        algorithm/RemoveUnusedArcsTest.cpp
        algorithm/ShiftFlowsTest.cpp
        algorithm/GetMaxDistanceTest.cpp
        algorithm/CreateBushTest.cpp)
