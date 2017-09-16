package com.artkostm.integrator.clump

protected[clump] trait Tuples {
  protected[clump] def normalize1[A, B] = (inputs: (A, B)) => inputs match {
    case (a, b) => ((a), b)
  }

  protected[clump] def normalize2[A, B, C] = (inputs: (A, B, C)) => inputs match {
    case (a, b, c) => ((a, b), c)
  }

  protected[clump] def normalize3[A, B, C, D] = (inputs: (A, B, C, D)) => inputs match {
    case (a, b, c, d) => ((a, b, c), d)
  }

  protected[clump] def normalize4[A, B, C, D, E] = (inputs: (A, B, C, D, E)) => inputs match {
    case (a, b, c, d, e) => ((a, b, c, d), e)
  }

  protected[clump] def denormalize1[A, B] = (trunk: A, last: B) => (trunk, last) match {
    case ((a), b) => (a, b)
  }

  protected[clump] def denormalize2[A, B, C] = (trunk: (A, B), last: C) => (trunk, last) match {
    case ((a, b), c) => (a, b, c)
  }

  protected[clump] def denormalize3[A, B, C, D] = (trunk: (A, B, C), last: D) => (trunk, last) match {
    case ((a, b, c), d) => (a, b, c, d)
  }

  protected[clump] def denormalize4[A, B, C, D, E] = (trunk: (A, B, C, D), last: E) => (trunk, last) match {
    case ((a, b, c, d), e) => (a, b, c, d, e)
  }

  protected[clump] def fetch1[A, B, C](fetch: (A, B) => Future[C]) = (params: (A), values: B) => (params, values) match {
    case ((a), b) => fetch(a, b)
  }

  protected[clump] def fetch2[A, B, C, D](fetch: (A, B, C) => Future[D]) = (params: (A, B), values: C) => (params, values) match {
    case ((a, b), c) => fetch(a, b, c)
  }

  protected[clump] def fetch3[A, B, C, D, E](fetch: (A, B, C, D) => Future[E]) = (params: (A, B, C), values: D) => (params, values) match {
    case ((a, b, c), d) => fetch(a, b, c, d)
  }

  protected[clump] def fetch4[A, B, C, D, E, F](fetch: (A, B, C, D, E) => Future[F]) = (params: (A, B, C, D), values: E) => (params, values) match {
    case ((a, b, c, d), e) => fetch(a, b, c, d, e)
  }
}
