// import * as bodyParser from "body-parser";
import * as cookieParser from "cookie-parser";
import * as cors from "cors";
import * as express from "express";
import { inject, injectable } from "inversify";
import * as logger from "morgan";
import { PresentationController } from "./controllers/presentation.controller";
import Types from "./types";

@injectable()
export class Application {

    private readonly internalError: number = 500;
    public app: express.Application;

    public constructor(@inject(Types.PresentationController) private presentationController: PresentationController) {
        this.app = express();
        this.config();
        this.bindRoutes();
    }

    private config(): void {
        // middlewares configuration
        this.app.use(logger("dev"));
        // this.app.use(bodyParser.json());
        // this.app.use(bodyParser.urlencoded({ extended: true }));
        this.app.use(cookieParser());
        this.app.use(cors());
    }

    public bindRoutes(): void {
        this.app.use("/", this.presentationController.router);
        this.errorHandeling();
    }

    private errorHandeling(): void {
        this.app.use((req: express.Request, res: express.Response, next: express.NextFunction) => {
            const err: Error = new Error("Not Found");
            next(err);
        });

        // development error handler
        // will print stacktrace
        if (this.app.get("env") === "development") {
            // tslint:disable-next-line:no-any
            this.app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
                res.status(err.status || this.internalError);
                res.send({
                    message: err.message,
                    error: err,
                });
            });
        }

        // production error handler
        // no stacktraces leaked
        // tslint:disable-next-line:no-any
        this.app.use((err: any, req: express.Request, res: express.Response, next: express.NextFunction) => {
            res.status(err.status || this.internalError);
            res.send({
                message: err.message,
                error: {},
            });
        });
    }
}
