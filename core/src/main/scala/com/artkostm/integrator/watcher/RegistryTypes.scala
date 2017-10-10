package com.artkostm.integrator.watcher

import java.nio.file.Path

object RegistryTypes {
  type Callback  = (Path) => Unit
  type Callbacks = List[Callback]
}