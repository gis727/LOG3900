import { NextFunction, Request, Response, Router } from "express";
import { inject, injectable } from "inversify";
import { PresentationService } from "../services/presentation.service";
import Types from "../types";

// DUMMY controller

@injectable()
export class PresentationController {

    public constructor(@inject(Types.PresentationService) public presentationService: PresentationService) { }

    public get router(): Router {
        const router: Router = Router();

        router.get("/",
                   async (req: Request, res: Response, next: NextFunction) => {
                res.json("All good. Nothing to see here...");
            });

        return router;
    }
}
