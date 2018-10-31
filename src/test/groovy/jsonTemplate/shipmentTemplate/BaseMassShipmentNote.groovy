package jsonTemplate.shipmentTemplate

class BaseMassShipmentNote {
    def noteseq
    def notetype
    def notevalue
    def notecode
    def notevisibility
    def stopseq
    BaseMassShipmentNote(){
        noteseq=''
        notetype=''
        notevalue=''
        notecode=''
        notevisibility=''
        stopseq = 0
    }
    def buildjson(parent) {

        parent."ShipmentNote" {
            NoteSeq this.noteseq
            NoteType this.notetype
            NoteValue this.notevalue
            NoteVisibility this.notevisibility
            NoteCode this.notecode
            StopSeq this.stopseq
        }
    }
}
