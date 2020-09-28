package com.nagpal.shivam.vtucslab.models

class LabResponse {
    var context: String? = null
    var isValid = true
    var invalidationMessage: String? = null
    var laboratories: Array<Laboratory>? = null
    var labExperiments: Array<LabExperiment>? = null
    var github_raw_content: String? = null
    var organization: String? = null
    var repository: String? = null
    var branch: String? = null
}