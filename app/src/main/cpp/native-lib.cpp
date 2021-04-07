#include <jni.h>
#include <string>
#include <cmath>

extern "C" JNIEXPORT jstring JNICALL
Java_com_yang_example_activity_MainActivity_stringFromJNI(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL Java_com_yang_example_utils_JNIRender_render(
        JNIEnv *env, jclass jclass1, jintArray b1,
        jintArray b2, jint width, jint height,
        jdouble r, jdouble len, jdouble weight,
        jdouble x0,
        jdouble y0) {
    jint *b3 = env->GetIntArrayElements(b1, JNI_FALSE);
    jint size = env->GetArrayLength(b1);
    jint *b4 = env->GetIntArrayElements(b2, JNI_FALSE);
    int offset;
    int i = 0;
    int length = width * height;
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++, i++) {

            double disance = sqrt((x0 - x) * (x0 - x) + (y0 - y) * (y0 - y));

            if (disance < r - len || disance > r + len) {//不在水波影响范围
                b4[i] = 0x00ffffff;
                continue;
            }
            double d = (disance - r) / len;//这个值在区间内由-1变到0再到1

            //在[-1,1]区间,形成水波曲线函数,
            d = cos(d * M_PI / 2) * -weight;//向外的像素偏移值;

            int dx = (int) (d * (x - x0) / r);//x方向的偏移值
            int dy = (int) (d * (y - y0) / r);//y方向的偏移值

            offset = dy * width + dx;// 计算出偏移象素和原始象素的内存地址偏移量 :

            // 判断坐标是否在范围内
            if (i + offset > 0 && i + offset < length) {
                b4[i] = b3[i + offset];
            } else {
                b4[i] = 0x00ffffff;
            }
        }
    }
    env->SetIntArrayRegion(b2, 0, size, b4);
}