package com.nagpal.shivam.vtucslab.core

sealed class Resource<D, E> {
  class Loading<D, E>(val data: D? = null) : Resource<D, E>()

  class Success<D, E>(val data: D) : Resource<D, E>()

  class Error<D, E>(val error: E) : Resource<D, E>()
}
