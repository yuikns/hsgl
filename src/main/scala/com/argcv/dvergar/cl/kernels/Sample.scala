package com.argcv.dvergar.cl.kernels

/**
  * @author yu
  */
object Sample {
  lazy val add_ab =
    """
      |__kernel void add_ab(__global const float* a,
      |         __global const float* b,
      |         __global float* out,
      |          int n)
      |{
      |     int i = get_global_id(0);
      |     if (i >= n)
      |        return;
      |     int pa = i == 0 ? 0 : a[i-1];
      |     int nb = i == n - 1 ? 0 : b[i+1];
      |     out[i] = a[i] * pa + b[i] * nb; // n
      |}
    """.stripMargin

}
