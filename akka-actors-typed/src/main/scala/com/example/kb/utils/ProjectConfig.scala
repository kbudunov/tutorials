package com.example.kb.utils

import com.typesafe.config.Config

object ProjectConfig {
  private val ConfigPath: String = "akka-actors-typed"

  def apply(rootConfig: Config): ProjectConfig = {
    val conf = rootConfig.getConfig(ConfigPath)
    new ProjectConfig(conf, SomeConfig(conf))
  }
}

case class ProjectConfig(rootConfig: Config, someConfig: SomeConfig)

object SomeConfig {
  private val FTPConfigPath = "some"
  private val Message       = "message"

  def apply(config: Config): SomeConfig = {
    val thisConfig   = config.getConfig(FTPConfigPath)
    val messageValue = thisConfig.getString(Message)

    new SomeConfig(
      messageValue
    )
  }
}

case class SomeConfig(message: String)
