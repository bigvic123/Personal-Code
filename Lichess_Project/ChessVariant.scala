package lila.app

import chess.{Board, Pos, Standard}

case object MyVariant extends Variant(
  id = 12,
  key = "myvariant",
  name = "My Variant",
  standardInitialPosition = true
) {

  override def pieces = Standard.pieces

  override def validMoves(board: Board, origin: Pos) = {
    val moves = super.validMoves(board, origin)
    if (board.pieceAt(origin).exists(_.role == Pawn)) {
      // Allow pawn to move two squares from any position
      moves ++= origin.pawnMoveForward(2)
    }
    moves
  }
}
