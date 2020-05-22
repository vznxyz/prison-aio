package net.evilblock.prisonaio.module.crate.roll

data class CrateRollStage(var started: Boolean = false,
                          var startedAt: Long = -1,
                          var stageLength: Long,
                          var onFinish: () -> Unit) {

    fun start() {
        started = true
        startedAt = System.currentTimeMillis()
    }

    fun finish() {
        onFinish.invoke()
    }

}